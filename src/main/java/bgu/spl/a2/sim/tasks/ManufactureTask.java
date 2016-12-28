package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by stephanieschustermann on 25/12/2016.
 */
public class ManufactureTask  extends Task<Product> {
    private String prodType;
    private Warehouse warehouse;
    private AtomicLong startId;

    public ManufactureTask(String productType, Warehouse warehouse, long startId){
        this.prodType = productType;
        this.warehouse = warehouse;
        this.startId  = new AtomicLong(startId);
    }

    @Override
    protected void start() {
        // get the plan of the product
        ManufactoringPlan plan = warehouse.getPlan(this.prodType);

        // the task's product
        Product result = new Product(this.startId.get(), this.prodType);

        // if the product's plan doesnt need parts - then complete the product
        if(plan.getParts() == null || plan.getParts().length == 0){
            result.setFinalId(result.getStartId());
            complete(result);
        } else {
            ArrayList<Task<Product>> tasks = new ArrayList<>();
            startId.incrementAndGet();
            // for each part spawn a manufacture task
            for (String part : plan.getParts()) {
                ManufactureTask task = new ManufactureTask(part, warehouse, startId.get());
                tasks.add(task);
                spawn(task);
            }

            //Assembling part:
            // add the task to when resolve
            whenResolved(tasks, () -> {
                // add each part to the product
                for(Task<Product> productTask: tasks){
                    result.addPart(productTask.getResult().get());
                }

                int toolsAmount = plan.getTools() == null ? 0 : plan.getTools().length;
                System.out.println("************* toolsAmount " + toolsAmount );
                CountDownLatch usedTools = new CountDownLatch(toolsAmount);
                final long[] finalId = {result.getStartId()};
                // for each tool get a when resolve callback
                for (String tool : plan.getTools()) {
                    Deferred<Tool> toolDeferred = warehouse.acquireTool(tool);
                    toolDeferred.whenResolved(() -> {
                        if(toolDeferred.get() == null){
                            System.out.println("null");
                        }
                        System.out.println("************* tool when resolve " + toolDeferred.get().getType() + " on product " + result.getName() );
                        finalId[0] += toolDeferred.get().useOn(result);
                        warehouse.releaseTool(toolDeferred.get());
                        usedTools.countDown();
//                        borrowedTools.add(toolDeferred.get());
//                        int count = this.whenResolveToolCounter.addAndGet(1);
//                        // if we got all the tools, then start assembling
//                        if (plan.getTools().length == count) {
//                            long finalId = result.getStartId();
//                            // use the useON function to get the id (sum them all)
//                            for(Tool borrowed: this.borrowedTools){
//                                finalId += borrowed.useOn(result);
//                            }
//
//                            result.setFinalId(finalId);
//                            // complete the task
//                            complete(result);
//
//                            // return the tools to warehouse
//                            for(Tool borrowed: this.borrowedTools){
//                                warehouse.releaseTool(borrowed);
//                            }
//                        }
                    });
                }

                try {
                    usedTools.await();

                    System.out.println("************* get all the tools of product " + result.getName() );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                result.setFinalId(finalId[0]);
                complete(result);
            });

        }
    }
}

