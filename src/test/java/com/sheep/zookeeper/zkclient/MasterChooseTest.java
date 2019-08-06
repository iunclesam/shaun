package com.sheep.zookeeper.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.util.ArrayList;

public class MasterChooseTest {

    private static final String connectionString = "192.168.1.102:2181";

    public static void main(String[] args) {
        ArrayList<MasterSelector> masterSelectors = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            ZkClient zkClient = new ZkClient(connectionString,
                    5000,
                    5000,
                    new SerializableSerializer());
            Server server = new Server();
            server.setId(i);
            server.setMachine("服务"+i);

            MasterSelector masterSelector = new MasterSelector(zkClient, server);
            masterSelectors.add(masterSelector);

            masterSelector.start();
        }
    }
}
