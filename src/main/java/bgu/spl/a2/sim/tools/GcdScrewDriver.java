package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.math.BigInteger;


public class GcdScrewDriver implements Tool {
    //not sure i understood what they wanted the tool type to be
    //in the instructions it says "a string describing the tool type"
    private String toolType = "gs-driver";
    private long resultOfToolUse;

    private long reverse(long n) {
        long reverse=0;
        while( n != 0 )
        {
            reverse = reverse * 10;
            reverse = reverse + n%10;
            n = n/10;
        }
        return reverse;
    }

    private long findGCD(long startId) {
        long rev = reverse(startId);
        BigInteger firstNum = new BigInteger(String.valueOf(rev));
        BigInteger secondNum = new BigInteger(String.valueOf(startId));
        return Long.parseLong(firstNum.gcd(secondNum).toString());
    }
    @Override
    public String getType() {
        return toolType;
    }

    @Override
    //i think this is what they meant
    public long useOn(Product p) {
        long value=p.getStartId();
        for(Product part : p.getParts()){
            value+=Math.abs(findGCD(part.getStartId()));
        }
        return value;
    }

}
