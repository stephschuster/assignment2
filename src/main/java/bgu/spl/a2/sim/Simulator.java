/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tasks.WaveTask;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import com.google.gson.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

    private static ParseJson parser;
    private static Warehouse warehouse;
    private static WorkStealingThreadPool pool;

    /**
     * Begin the simulation
     * Should not be called before attachWorkStealingThreadPool()
     */
    public static ConcurrentLinkedQueue<Product> start() {
        //create a new warehouse and add the tools to it
        warehouse = new Warehouse();

        for (int i = 0; i < parser.getTools().size(); i++) {
            if (parser.getTools().get(i).getName().equalsIgnoreCase("rs-pliers")) {
                warehouse.addTool(new RandomSumPliers(), parser.getTools().get(i).getQty());
            } else if (parser.getTools().get(i).getName().equalsIgnoreCase("np-hammer")) {
                warehouse.addTool(new NextPrimeHammer(), parser.getTools().get(i).getQty());
            } else {
                warehouse.addTool(new GcdScrewDriver(), parser.getTools().get(i).getQty());
            }
        }

        for (int i = 0; i < parser.getPlans().size(); i++) {
            ParsePlan parsePlan =  parser.getPlans().get(i);
            ManufactoringPlan plan = new ManufactoringPlan(parsePlan.getProduct(), parsePlan.getParts(), parsePlan.getTools());
            warehouse.addPlan(plan);
        }

        ConcurrentLinkedQueue<Product> result = new ConcurrentLinkedQueue<>();
        pool.start();
        LinkedList<String> rightOrderString = new LinkedList<>();

        for (int i = 0; i < parser.getWaves().size(); i++) {
            List<ParseWave> parseWave = parser.getWaves().get(i);
            CountDownLatch downLatch = new CountDownLatch(parseWave.size());

            for(ParseWave product : parseWave){
                WaveTask task = new WaveTask(product.getProduct(), warehouse, product.getStartId(), product.getQty());
                pool.submit(task);
                //add the products name in the right order (how they will show at the end)
                for (int j = 0; j < product.getQty(); j++) {
                    rightOrderString.add(product.getProduct());
                }

                task.getResult().whenResolved(() -> {
                    // add the products to the concurrent
                        for (Product product1 : task.getResult().get()) {
                            result.add(product1);
                        }
                    //let the next wave begin
                    downLatch.countDown();
                });


            }

            try {
                downLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //create a new list and transfer the products in the right order
        ConcurrentLinkedQueue<Product> finalResult = new ConcurrentLinkedQueue<>();
        for (String str: rightOrderString) {
            for(Product aa:result) {
                if(aa.getName().equalsIgnoreCase(str)) {
                    finalResult.add(aa);
                    result.remove(aa);
                    str = "already in list";
                }
            }
        }

        return finalResult;
    }

    /**
     * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
     *
     * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
     */
    public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool) {
        pool = myWorkStealingThreadPool;
    }

    //they gave us a main funccton that returns an int
    //why does this main function need to return an int?
    //just for testing i changed it to void
    public static void main(String[] args)  {

        for(int i = 0; i < 500; i++){
            //first parse the json file
            Reader reader = null;
            File myArgs = new File(args[0]);
            try {
                reader = new FileReader(myArgs);
            } catch (FileNotFoundException e) {
            }

            GsonBuilder gBuilder = new GsonBuilder();
            gBuilder.registerTypeAdapter(ParseJson.class, new Deserializer());
            Gson gson = gBuilder.create();
            parser = gson.fromJson(reader, ParseJson.class);
            WorkStealingThreadPool pool = new WorkStealingThreadPool(parser.getThreads());
            attachWorkStealingThreadPool(pool);


            //output the linked queue
            ConcurrentLinkedQueue<Product> SimulationResult;
            SimulationResult = start();
            FileOutputStream fout = null;
            ObjectOutputStream oos = null;

            try {
                fout = new FileOutputStream("result.ser");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                oos = new ObjectOutputStream(fout);
                oos.writeObject(SimulationResult);
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("**************** " + i);

        }
    }
}

