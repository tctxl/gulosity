package com.opdar.gulosity.base;

import java.util.LinkedList;
import java.util.Map;

/**
 * Created by 俊帆 on 2016/10/14.
 */
public interface TableCacheCallback {
    public void callback(Map<String,LinkedList<String>> tables);
}
