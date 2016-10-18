package com.opdar.gulosity.persistence;


/**
 * Created by 俊帆 on 2016/10/17.
 */
public interface IPersistence {
    public void commit(long position);
    public long getPosition();
    public String getFileName();
    public void setFileName(String fileName);
}
