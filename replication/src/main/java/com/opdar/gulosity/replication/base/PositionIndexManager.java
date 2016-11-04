package com.opdar.gulosity.replication.base;

import java.io.*;
import java.util.TreeMap;

/**
 * position索引类
 * Created by 俊帆 on 2016/11/4.
 */
public class PositionIndexManager {

    //position,length
    private TreeMap<Long, Integer> map = new TreeMap<Long, Integer>();

    File positionFile = null;
    //索引文件版本
    private int version = 1;

    public PositionIndexManager() {
        String file = new File(new File(Registry.FILE_PATH).getAbsolutePath()).getParent();
        positionFile = new File(file, ".position");
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(Registry.FILE_PATH, "r");
            if (randomAccessFile.getChannel().size() > 0) {
                version = randomAccessFile.readInt();
            }
        } catch (FileNotFoundException ignored) {
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
        //load indexs
        RandomAccessFile randomAccessFile1 = null;
        try {
            randomAccessFile1 = new RandomAccessFile(positionFile, "r");
            long pos = 0;
            while ((pos = randomAccessFile1.readLong()) != -1) {
                map.put(pos, randomAccessFile1.readInt());
            }
        } catch (Exception ignored) {
        } finally {
            try {
                if (randomAccessFile1 != null) {
                    randomAccessFile1.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //重建索引
    public void rebuild() {
        delete();
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(Registry.FILE_PATH, "r");
            version = randomAccessFile.readInt();
            int length = 0;
            map.clear();
            long pos = 8;
            DataOutputStream dataOutputStream = null;
            try {
                dataOutputStream = new DataOutputStream(new FileOutputStream(positionFile, true));
                while ((length = randomAccessFile.readInt()) != -1) {
                    dataOutputStream.writeLong(pos);
                    dataOutputStream.writeInt(length);
                    map.put(pos, length);
                    pos += length + 8;
                    randomAccessFile.skipBytes(length);
                }

            } catch (Exception ignored) {
            } finally {
                try {
                    if (dataOutputStream != null) {
                        dataOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
    }

    public void delete() {
        positionFile.delete();
    }

    public int getVersion() {
        return version;
    }

    //新增索引
    public void addIndex(long position, int length) {
        DataOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new DataOutputStream(new FileOutputStream(positionFile, true));
            dataOutputStream.writeLong(position);
            dataOutputStream.writeInt(length);
        } catch (Exception ignored) {
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        map.put(position, length);
    }

}