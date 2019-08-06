package com.sheep.zookeeper.javaapiclient;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperClient {
    private static final String connectString = "192.168.1.102:2181";
    private static int sessionTimeout = 2000;

    public static ZooKeeper getInstance() throws InterruptedException, IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        return zooKeeper;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(ZookeeperClient.getInstance());
    }

    public static int getSessionTimeout() {
        return sessionTimeout;
    }
}
