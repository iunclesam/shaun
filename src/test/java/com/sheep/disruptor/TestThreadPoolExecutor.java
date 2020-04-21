package com.sheep.disruptor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TestThreadPoolExecutor {
    private static Logger logger = Logger.getLogger("TestThreadPoolExecutor");

    public static void main(String[] args) throws InterruptedException {

        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(1024),
                new ThreadFactory() {
                    private final AtomicInteger cnt = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("order-thread-" + cnt.getAndIncrement());
                        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                            @Override
                            public void uncaughtException(Thread t, Throwable e) {
                                System.out.println(t);
                                e.printStackTrace();
                            }
                        });
                        return t;
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println(((T)r).order);
                    }
                });

        for (int i = 0; i < Short.MAX_VALUE; i++) {
            pool.execute(new T(new Order(i)));
        }


        Thread.sleep(10 * 1000);
        pool.shutdown();
    }

    static class T implements Runnable {
        private final Order order;

        public T(Order order) {
            this.order = order;
        }

        @Override
        public void run() {

            if (order.id.hashCode() % Short.MAX_VALUE == 0) {
                throw new IllegalArgumentException();
            }
        }
    }

    static class Order {
        private final Integer id;

        public Order(Integer id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Order[id=" + id +"]";
        }
    }
}
