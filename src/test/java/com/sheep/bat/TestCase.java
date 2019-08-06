package com.sheep.bat;

import org.junit.Test;

import java.util.concurrent.TimeUnit;


public class TestCase {
    public static void main(String[] args) {
        Annimal.t();
        Cat.t();
    }

    @Test
    public void testThreadJoin() throws InterruptedException {
        final Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t1");
            }
        });
        final Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    t1.join();
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t2");
            }
        });
        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    t2.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("t3");
            }
        });
        t1.start();
        t2.start();
        t3.start();

        t3.join();
    }
}
