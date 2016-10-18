package com.opdar.gulosity.entity;

import java.io.Serializable;

/**
 * Created by 俊帆 on 2016/10/18.
 */
public class Position implements Serializable{
    private long nextPosition;
    private String fileName;
    private int serverId;

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public long getNextPosition() {
        return nextPosition;
    }

    public void setNextPosition(long nextPosition) {
        this.nextPosition = nextPosition;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
