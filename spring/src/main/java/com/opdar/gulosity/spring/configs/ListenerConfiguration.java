package com.opdar.gulosity.spring.configs;

import com.opdar.gulosity.base.RowCallback;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class ListenerConfiguration {
    private List<RowCallback> callbacks = new LinkedList<RowCallback>();
    public void add(RowCallback rowCallback){
        callbacks.add(rowCallback);
    }
}
