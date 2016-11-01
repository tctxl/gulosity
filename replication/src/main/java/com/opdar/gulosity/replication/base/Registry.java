package com.opdar.gulosity.replication.base;

import com.opdar.gulosity.replication.server.base.IoSession;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class Registry {
    //store file path
    public static final String FILE_PATH = "file.dat";
    public static final ConcurrentHashMap<String, IoSession> registryClients = new ConcurrentHashMap<String, IoSession>();

    public static IoSession remove(Object key) {
        return registryClients.remove(key);
    }

    public static IoSession put(String key, IoSession value) {
        return registryClients.put(key, value);
    }

    public static int size() {
        return registryClients.size();
    }

    public static IoSession get(Object key) {
        return registryClients.get(key);
    }
}
