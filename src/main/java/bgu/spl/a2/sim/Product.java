package bgu.spl.a2.sim;

import java.util.LinkedList;
import java.util.List;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product {
	/**
	* Constructor 
	* @param startId - Product start id
	* @param name - Product name
	*/
	private final long id;
	private long finalId;
	private String name;
	//maybe we dont want a linkedlist here
	private LinkedList<Product> partsList;

    public Product(long startId, String name) {
    	this.name = name;
    	this.id = startId;
    	partsList = new LinkedList<>();
	}

	/**
	* @return The product name as a string
	*/
    public String getName() {
    	return this.name;
	}

	/**
	* @return The product start ID as a long. start ID should never be changed.
	*/
    public long getStartId() {
    	return this.id;
	}
    
	/**
	* @return The product final ID as a long. 
	* final ID is the ID the product received as the sum of all UseOn(); 
	*/
	// TODO add the sum of all the parts
    public long getFinalId() {
    	return finalId;
	}

	public void setFinalId(long finalId) {
		this.finalId = finalId;
	}

	/**
	* @return Returns all parts of this product as a List of Products
	*/
    public List<Product> getParts() {
    	return partsList;
	}

	/**
	* Add a new part to the product
	* @param p - part to be added as a Product object
	*/
    public void addPart(Product p) {
    	partsList.add(p);
	}


}
