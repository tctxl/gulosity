package com.opdar.gulosity.base;

import com.opdar.gulosity.event.binlog.TableMapEvent;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局上下文
 * Created by Shey on 2016/8/25.
 */
public class MysqlContext {

    private static ConcurrentHashMap<Class<?>, Object> objects = new ConcurrentHashMap<Class<?>, Object>();
    private static ConcurrentHashMap<Long, TableMapEvent> tables = new ConcurrentHashMap<Long, TableMapEvent>();
    private static LinkedList<RowCallback> rowCallbacks = new LinkedList<RowCallback>();

    public static <T> T get(Class<T> eventCls) {
        return objects.containsKey(eventCls)? (T) objects.get(eventCls) :null;
    }

    public static void addRowCallback(RowCallback callback) {
        if (callback != null) {
            rowCallbacks.add(callback);
        }
    }

    public static LinkedList<RowCallback> getRowCallbacks() {
        return rowCallbacks;
    }

    public static <T>void add(T event) {
        objects.put(event.getClass(), event);
    }

    public static void addTable(TableMapEvent table){
        tables.put(table.getTableId(),table);
    }

    public static TableMapEvent getTable(Long id){
        return tables.get(id);
    }
}
