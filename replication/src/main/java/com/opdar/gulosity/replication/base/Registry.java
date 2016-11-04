package com.opdar.gulosity.replication.base;

import com.opdar.gulosity.replication.server.base.IoSession;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class Registry {
    //store file path
    public static String FILE_PATH = "file.dat";
    public static int MAX_SIZE = 10240;

    public static final ConcurrentHashMap<String, IoSession> registryClients = new ConcurrentHashMap<String, IoSession>();

    public static IoSession remove(String key) {
        return registryClients.remove(key);
    }

    public static IoSession put(String key, IoSession value) {
        return registryClients.put(key, value);
    }

    public static int size() {
        return registryClients.size();
    }

    public static boolean containsKey(String key) {
        return registryClients.containsKey(key);
    }

    public static IoSession get(String key) {
        return registryClients.get(key);
    }

    public static void notifyClients(long position, long nextPosition) {
        byte[] b = null;
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(Registry.FILE_PATH, "r");
            randomAccessFile.seek(position);
            int length = randomAccessFile.readInt();
            b = new byte[length];
            randomAccessFile.read(b);
        } catch (Exception ignored) {
        } finally {
            if (randomAccessFile != null) try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null != b)
            for (IoSession session : registryClients.values()) {
                session.writeLog(nextPosition, b);
            }
    }
}
