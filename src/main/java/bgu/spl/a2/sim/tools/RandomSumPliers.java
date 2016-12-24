package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.util.Random;


public class RandomSumPliers implements Tool {
    private String toolType = "rs-pliers";
    @Override
    public String getType() {
        return toolType;
    }

    private long sum(long seed) {
        long result = 0;
        Random rnd = new Random(seed);
        for(int i=0; i<(seed%10000); i++) {
            result += rnd.nextInt();
        }
        return result;
    }

    @Override
    public long useOn(Product p) {
        return sum(p.getStartId());
    }
}
