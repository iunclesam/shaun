package com.sheep.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

public class TestInterrupt {
    public static void main(String[] args) throws InterruptedException {

        /*Thread t = new Thread(new Runnable() {
            AtomicInteger cnt = new AtomicInteger(0);
            @Override
            public void run() {
                boolean exit = false;
                while (!Thread.currentThread().isInterrupted() && !exit) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        exit = true;
                    }
                    System.out.println(cnt.getAndIncrement());
                }
            }
        });
        t.start();
        Thread.sleep(1000);
        t.interrupt();*/


        Server server = new Server();
        new Thread(server).start();

        System.out.println(Thread.currentThread().getName());
        server.stop();

    }

    static class Server implements Runnable {

        private volatile boolean exit = false;

        @Override
        public void run() {
            while (!exit) {
                System.out.println("Server is running.....");
            }

            System.out.println("Server is stopped.....");
        }

        public void stop() {
            exit = true;
        }
    }
}
