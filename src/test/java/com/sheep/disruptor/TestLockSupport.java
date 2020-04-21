package com.sheep.disruptor;

import java.util.concurrent.locks.LockSupport;

public class TestLockSupport {

    public static void main(String[] args) throws Exception{
        Thread t = new Thread(()->{
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += i;
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LockSupport.park();

            System.out.println("sum = " + sum);
        });

        t.start();

        Thread.sleep(1000);
        //LockSupport.unpark(t);
        t.interrupt();
    }
}
