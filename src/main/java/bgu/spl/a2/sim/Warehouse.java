
package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.Deferred;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
	LinkedList<ManufactoringPlan> plans = new LinkedList<>();
	ConcurrentHashMap<String, ConcurrentLinkedQueue<Tool>> tools = new ConcurrentHashMap<>();
	ConcurrentHashMap<String, ConcurrentLinkedQueue<Deferred<Tool>>> waitingList = new ConcurrentHashMap<>();

	/**
	* Constructor
	*/
    public Warehouse(){}

	/**
	 * Tool acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * @param type - string describing the required tool
	 * @return a deferred promise for the  requested tool
	 */
	public Deferred<Tool> acquireTool(String type){
		Deferred<Tool> promise = new Deferred<>();
		if(this.tools.get(type).size() > 0){
			Tool tool = this.tools.get(type).poll();
			promise.resolve(tool);
		} else {
			ConcurrentLinkedQueue<Deferred<Tool>> list = waitingList.get(type);
            if(list == null)
                list = new ConcurrentLinkedQueue<>();
			list.add(promise);
			waitingList.put(type, list);
		}
		return promise;
	}

	/**
	 * Tool return procedure - releases a tool which becomes available in the warehouse upon completion.
	 * @param tool - The tool to be returned
	 */
	public void releaseTool(Tool tool){

		ConcurrentLinkedQueue<Deferred<Tool>> list = waitingList.get(tool.getType());

		if(list != null && list.size() > 0)
			list.poll().resolve(tool);
		else{
			ConcurrentLinkedQueue<Tool> temp = this.tools.get(tool.getType());

			temp.add(tool);
			this.tools.put(tool.getType(), temp);
		}
	}


	/**
	 * Getter for ManufactoringPlans
	 * @param product - a string with the product name for which a ManufactoringPlan is desired
	 * @return A ManufactoringPlan for product
	 */
	public ManufactoringPlan getPlan(String product){
		for (ManufactoringPlan plan: plans) {
			if(plan.getProductName().equals(product))
				return plan;
		}

		return null;
	}

	/**
	 * Store a ManufactoringPlan in the warehouse for later retrieval
	 * @param plan - a ManufactoringPlan to be stored
	 */
	public void addPlan(ManufactoringPlan plan){
		this.plans.add(plan);
	}

	/**
	 * Store a qty Amount of tools of type tool in the warehouse for later retrieval
	 * @param tool - type of tool to be stored
	 * @param qty - amount of tools of type tool to be stored
	 */
	public void addTool(Tool tool, int qty){
		ConcurrentLinkedQueue<Tool> temp = this.tools.get(tool.getType());
		if(temp == null)
			temp = new ConcurrentLinkedQueue<>();

		for(int i = 0; i < qty; i++) {
			ConcurrentLinkedQueue<Deferred<Tool>> list = waitingList.get(tool.getType());
			if (list != null && list.size() > 0)
				list.poll().resolve(tool);
			else {
				temp.add(tool);
				this.tools.put(tool.getType(), temp);
			}
		}
	}
}
