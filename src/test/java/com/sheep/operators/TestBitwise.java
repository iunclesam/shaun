package com.sheep.operators;

import org.junit.Test;

public class TestBitwise {

    @Test
    public void test(){
        int n = 17;
        System.out.println(Integer.toBinaryString(n));
        System.out.println(Integer.toBinaryString(n));
        System.out.println(n >>> 1);

        int count = 1;
        Boolean b = new Boolean(count>=1);
        System.out.println(b);

    }
}
