package com.opdar.gulosity.replication.server.protocol;

import com.opdar.gulosity.replication.base.Registry;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class RequestLog {
    private int position;

    public int getPosition() {
        return position;
    }

    public byte[] read() {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(Registry.FILE_PATH, "r");
            randomAccessFile.seek(position);
            int length = randomAccessFile.readInt();
            byte[] b = new byte[length];
            randomAccessFile.read(b);
            return b;
        } catch (Exception ignored) {}finally {
            if(randomAccessFile != null) try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
