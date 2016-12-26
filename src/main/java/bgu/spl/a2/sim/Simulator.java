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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
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
        System.out.println("**************Tools***************");

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

        System.out.println("**************Plans***************");

        for (int i = 0; i < parser.getPlans().size(); i++) {
            ParsePlan parsePlan =  parser.getPlans().get(i);
            ManufactoringPlan plan = new ManufactoringPlan(parsePlan.getProduct(), parsePlan.getParts(), parsePlan.getTools());
            warehouse.addPlan(plan);
        }

        ConcurrentLinkedQueue<Product> result = new ConcurrentLinkedQueue<>();
        pool.start();

        System.out.println("**************Waves***************");
        for (int i = 0; i < parser.getWaves().size(); i++) {
            List<ParseWave> parseWave = parser.getWaves().get(i);

            CountDownLatch l = new CountDownLatch(parseWave.size());

            for(ParseWave product : parseWave){
                System.out.println("product wave: " + product.getProduct());
                WaveTask task = new WaveTask(product.getProduct(), warehouse, product.getStartId(), product.getQty());
                pool.submit(task);

                task.getResult().whenResolved(() -> {
                    System.out.println("wave task resolved");
                    l.countDown();
                   // add the products to the concurrent
                    for(Product product1: task.getResult().get()){
                        result.add(product1);
                    }
                });
            }

            try {
                l.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("after l await");
        }

        try {
            pool.shutdown();
            System.out.println("pool shutdown");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
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
        //first parse the json file
        Reader reader = null;
        try {
            reader = new FileReader("myJ.txt");
        } catch (FileNotFoundException e) {
        }
        GsonBuilder gBuilder = new GsonBuilder();
        gBuilder.registerTypeAdapter(ParseJson.class, new Deserializer());
        Gson gson = gBuilder.create();
        parser = gson.fromJson(reader, ParseJson.class);
        WorkStealingThreadPool pool = new WorkStealingThreadPool(parser.getThreads());
        attachWorkStealingThreadPool(pool);
        start();
    }
}

