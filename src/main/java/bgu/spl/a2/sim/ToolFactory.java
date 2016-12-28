package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;

/**
 * Created by stephanieschustermann on 28/12/2016.
 */
public class ToolFactory {
    public static Tool getCopyOfTool(Tool tool){
        switch (tool.getType()){
            case("gs-driver"):
                return new GcdScrewDriver();
            case("np-hammer"):
                return new NextPrimeHammer();
            case("rs-pliers"):
                return new RandomSumPliers();
            default:
                return null;
        }
    }
}
