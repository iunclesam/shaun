package com.sheep.zookeeper.zkclient;

import java.io.Serializable;

/**
 * 服务
 */
public class Server implements Serializable{
    private int id;
    private String ip;
    private String machine;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", machine='" + machine + '\'' +
                '}';
    }
}
