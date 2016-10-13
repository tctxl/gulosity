package com.opdar.gulosity.connection.entity;

/**
 * Created by Shey on 2016/8/21.
 */
public class SalveEntity {
    private String file;
    private int position;
    private int salveId;

    public int getSalveId() {
        return salveId;
    }

    public void setSalveId(int salveId) {
        this.salveId = salveId;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean checkBinlogFile(){
        return file != null && !file.equals("");
    }
}
