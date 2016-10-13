package com.opdar.gulosity.entity;

import java.net.InetSocketAddress;

/**
 * Created by Shey on 2016/8/19.
 */
public class MysqlAuthInfoEntity {
    private InetSocketAddress address;
    private String userName;
    private String passWord;
    private String databaseName;

    public MysqlAuthInfoEntity(InetSocketAddress address, String userName, String passWord) {
        this.address = address;
        this.userName = userName;
        this.passWord = passWord;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
