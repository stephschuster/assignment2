package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.Deferred;

import java.awt.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * A class representing the warehouse in your simulation
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {
	private ConcurrentLinkedDeque<GcdScrewDriver> screwList;
	private ConcurrentLinkedDeque<NextPrimeHammer> hammerList;
	private ConcurrentLinkedDeque<RandomSumPliers> pliersList;
	private LinkedList<ManufactoringPlan> planList;


	/**
	 * Constructor
	 */
	public Warehouse(){
		pliersList = new ConcurrentLinkedDeque<>();
		hammerList = new ConcurrentLinkedDeque<>();
		screwList = new ConcurrentLinkedDeque<>();
		planList = new LinkedList<>();
	}

	/**
	 * Tool acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * @param type - string describing the required tool
	 * @return a deferred promise for the  requested tool
	 */
	public Deferred<Tool> acquireTool(String type) {

		return null;
	}

	/**
	 * Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	 * @param tool - The tool to be returned
	 */
	public void releaseTool(Tool tool) {
		//that's it?
		//what does it mean "upon completion?"
		addTool(tool, 1);
	}


	/**
	 * Getter for ManufactoringPlans
	 * @param product - a string with the product name for which a ManufactoringPlan is desired
	 * @return A ManufactoringPlan for product
	 */
	public ManufactoringPlan getPlan(String product) {
		//do we need to check every product name or is there a more general way to do this?
	return null;
	}

	/**
	 * Store a ManufactoringPlan in the warehouse for later retrieval
	 * @param plan - a ManufactoringPlan to be stored
	 */
	public void addPlan(ManufactoringPlan plan){
		//anything else here?
		planList.add(plan);
	}

	/**
	 * Store a qty Amount of tools of type tool in the warehouse for later retrieval
	 * @param tool - type of tool to be stored
	 * @param qty - amount of tools of type tool to be stored
	 */
	public void addTool(Tool tool, int qty){

		//i wans't sure if we are making a warehouse for just these type of tools or any tool that they want.
		//for now i did just for the tools that are in the json file
		if(tool.getType().equalsIgnoreCase("rs-pliers")) {
			for (int i = 0; i < qty; i++) {
				pliersList.add(new RandomSumPliers());
			}
		} else if(tool.getType().equalsIgnoreCase("np-hammer")) {
			for (int i = 0; i < qty; i++) {
				hammerList.add(new NextPrimeHammer());
			}
		} else if(tool.getType().equalsIgnoreCase( "gs-driver")) {
			for (int i = 0; i < qty; i++) {
				screwList.add(new GcdScrewDriver());
			}
		}

	}

}
