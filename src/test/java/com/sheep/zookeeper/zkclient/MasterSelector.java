package com.sheep.zookeeper.zkclient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * master选举
 */
public class MasterSelector {
    private static final String MASTER_PATH = "/master";

    private ZkClient zkClient;

    private IZkDataListener zkDataListener;

    private Server server;

    private Server master;

    private volatile boolean isRunning;

    private ScheduledExecutorService scheduledExec = Executors.newScheduledThreadPool(1);

    public MasterSelector(ZkClient zkClient, Server server) {
        this.zkClient = zkClient;
        this.server = server;
        this.zkDataListener = new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception { }
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                chooseMaster();
            }
        };
    }



    public void start() {
        if (!isRunning) {
            isRunning = true;
            zkClient.subscribeDataChanges(MASTER_PATH, zkDataListener);
            chooseMaster();
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            zkClient.unsubscribeDataChanges(MASTER_PATH, zkDataListener);
            releaseMaster();
            scheduledExec.shutdown();

        }
    }

    private void chooseMaster() {
        if(!isRunning){
            System.out.println("当前服务还没有启动");
            return;
        }

        try {
            zkClient.createEphemeral(MASTER_PATH, server);
            master = server;
            System.out.println(master + "选主成功");

            scheduledExec.scheduleWithFixedDelay(()->{
                System.out.println(Thread.currentThread().getName());
                stop();
            }, 0, 5000, TimeUnit.MILLISECONDS);
        }catch(ZkNodeExistsException e){
            Server master = zkClient.readData(MASTER_PATH, true);
            if (master == null) {
                chooseMaster();
            } else {
                this.master = master;
            }
        }

    }

    /**
     * 释放主(模拟故障弃权)
     */
    private void releaseMaster() {
        if (checkIsMaster()) {
            System.out.println(master + "释放主");
            zkClient.deleteRecursive(MASTER_PATH);
        }
    }

    private boolean checkIsMaster() {
        Server master = zkClient.readData(MASTER_PATH);
        if(server.getMachine().equals(master.getMachine())){
            this.master = master;
            return true;
        }
        return false;
    }
}
