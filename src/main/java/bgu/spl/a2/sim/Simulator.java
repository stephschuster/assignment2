/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

//        System.out.println(" Number of Threads= "+parser.getThreads());
//        System.out.println("**************Tools***************");
//
//        for (int i = 0; i < parser.getTools().size(); i++) {
//            System.out.println("Tool Name : "+parser.getTools().get(i).getName() +" qty= "+ parser.getTools().get(i).getQty());
//
//        }
//        System.out.println("**************Plans***************");
//
//        for (int i = 0; i < parser.getPlans().size(); i++) {
//            System.out.println("Product : " + parser.getPlans().get(i).getProduct());
//            for (int j = 0; j < parser.getPlans().get(i).getTools().length; j++) {
//                System.out.println(" Tool number "+j+" for product "+parser.getPlans().get(i).getProduct()+ " is "+
//                        parser.getPlans().get(i).getTools()[j]);
//            }
//            for (int j = 0; j < parser.getPlans().get(i).getParts().length; j++) {
//                System.out.println(" Part number "+j+" for product "+parser.getPlans().get(i).getProduct()+ " is "+
//                        parser.getPlans().get(i).getParts()[j]);
//            }
//        }
//        System.out.println("**************Waves***************");
//        for (int i = 0; i < parser.getWaves().size(); i++) {
//            for (int j = 0; j < parser.getWaves().get(i).size(); j++) {
//                System.out.println("Product Name : " + parser.getWaves().get(i).get(j).getProduct() +
//                        " qty= " + parser.getWaves().get(i).get(j).getQty() +
//                        " stardId: " + parser.getWaves().get(i).get(j).getStartId());
//            }
//        }

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



            return null;
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

