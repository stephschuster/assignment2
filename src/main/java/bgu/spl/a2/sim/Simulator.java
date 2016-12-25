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


/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

    private static ParseJson parser;
    private static Warehouse warehouse;

    /**
     * Begin the simulation
     * Should not be called before attachWorkStealingThreadPool()
     */
    public static ConcurrentLinkedQueue<Product> start() {
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
        System.out.println("**************Waves***************");
        for (int i = 0; i < parser.getWaves().size(); i++) {
            WorkStealingThreadPool pool = new WorkStealingThreadPool(parser.getThreads());
            pool.start();
            List<ParseWave> parseWave = parser.getWaves().get(i);
            for(ParseWave product : parseWave){
                WaveTask task = new WaveTask(product.getProduct(), warehouse, product.getStartId(), product.getQty());
                pool.submit(task);

                task.getResult().whenResolved(() -> {
                   // add the products to the concurrent
                    for(Product product1: task.getResult().get()){
                        result.add(product1);
                    }
                });
            }

            try {
                pool.shutdown();
            } catch (InterruptedException e) {

            }
        }

        return result;
    }

    /**
     * attach a WorkStealingThreadPool to the Simulator, this WorkStealingThreadPool will be used to run the simulation
     *
     * @param myWorkStealingThreadPool - the WorkStealingThreadPool which will be used by the simulator
     */
    public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool) {
    }

    //they gave us a main funccton that returns an int
    //why does this main function need to return an int?
    //just for testing i changed it to void
    public static void main(String[] args)  {
            start();
        }
}

