package bgu.spl.a2.sim.conf;

/**
 * a class that represents a manufacturing plan.
 *
 **/
public class ManufactoringPlan {

	private String productName;
	private String[] arrayParts;
	private String[] arrayTools;


	/** ManufactoringPlan constructor
	* @param product - product name
	* @param parts - array of strings describing the plans part names
	* @param tools - array of strings describing the plans tools names
	*/
    public ManufactoringPlan(String product, String[] parts, String[] tools) {
    	this.productName = product;
    	//not sure if we need to deep copy or shallow, did deep for now
    	for(int i=0; i<parts.length; i++) {
    		arrayParts[i] = parts[i];
		}
		for(int i=0; i<tools.length; i++) {
			arrayTools[i] = tools[i];
		}
	}

	/**
	* @return array of strings describing the plans part names
	*/
    public String[] getParts() {
    	return arrayParts;
	}

	/**
	* @return string containing product name
	*/
    public String getProductName() {
    	return productName;
	}
	/**
	* @return array of strings describing the plans tools names
	*/
    public String[] getTools() {
    	return arrayTools;
	}

}
