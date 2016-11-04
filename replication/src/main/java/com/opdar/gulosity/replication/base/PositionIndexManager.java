package com.opdar.gulosity.replication.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * position索引类
 * Created by 俊帆 on 2016/11/4.
 */
public class PositionIndexManager {

    //position,length
    private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

    File positionFile = null;
    //索引文件版本
    private int version = 1;

    public PositionIndexManager() {
        String file = new File(new File(Registry.FILE_PATH).getAbsolutePath()).getParent();
        positionFile = new File(file,".position");
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(Registry.FILE_PATH,"r");
            if(randomAccessFile.getChannel().size() > 0){
                version = randomAccessFile.readInt();
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //重建索引
    public void rebuild() {
        delete();
    }

    public void delete() {
        positionFile.delete();
    }

    public int getVersion() {
        return version;
    }

    //新增索引
    public void addIndex(int position,int length) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile= new RandomAccessFile(positionFile,"rw");
            randomAccessFile.writeInt(position);
            randomAccessFile.writeInt(length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        map.put(position,length);
    }

}