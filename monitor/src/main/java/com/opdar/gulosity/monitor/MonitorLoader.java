package com.opdar.gulosity.monitor;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 当前位置、文件名
 * Created by 俊帆 on 2016/10/26.
 */
public class MonitorLoader extends URLClassLoader {
    public MonitorLoader() {
        super(new URL[]{}, Thread.currentThread().getContextClassLoader());
    }
}
