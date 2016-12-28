package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.math.BigInteger;


public class NextPrimeHammer implements Tool {
    private String toolType = "np-hammer";
    private long resultOfToolUse;


    private long findNextPrime(long n) {
        BigInteger b = new BigInteger(String.valueOf(n));
        return Long.parseLong(b.nextProbablePrime().toString());
    }

    @Override
    public String getType() {
        return toolType;
    }

    @Override
    public long useOn(Product p) {
        long value = 0;
        for (Product part : p.getParts()) {
            value += Math.abs(findNextPrime(part.getFinalId()));
        }

        return value;
    }
}

