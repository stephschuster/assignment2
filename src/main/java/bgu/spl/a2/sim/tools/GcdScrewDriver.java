package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.math.BigInteger;


public class GcdScrewDriver implements Tool {
    //not sure i understood what they wanted the tool type to be
    //in the instructions it says "a string describing the tool type"
    private String toolType = "GCD screw driver";
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
    @Override
    public String getType() {
        return toolType;
    }

    @Override
    public long useOn(Product p) {
        long rev = reverse(p.getStartId());
        BigInteger firstNum = new BigInteger(String.valueOf(rev));
        BigInteger secondNum = new BigInteger(String.valueOf(p.getStartId()));
        resultOfToolUse = Long.parseLong(firstNum.gcd(secondNum).toString());

        return resultOfToolUse;
    }
}
