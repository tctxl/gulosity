package com.opdar.gulosity.base;

import com.opdar.gulosity.event.binlog.TableMapEvent;
import com.opdar.gulosity.persistence.FilePersistence;
import com.opdar.gulosity.persistence.IPersistence;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局上下文
 * Created by Shey on 2016/8/25.
 */
public class MysqlContext {

    private static ConcurrentHashMap<Class<?>, Object> objects = new ConcurrentHashMap<Class<?>, Object>();
    private static ConcurrentHashMap<Long, TableMapEvent> tables = new ConcurrentHashMap<Long, TableMapEvent>();
    private static LinkedList<RowCallback> rowCallbacks = new LinkedList<RowCallback>();
    private static Map<String, LinkedList<String>> tablesCache = new HashMap<String, LinkedList<String>>();
    private static IPersistence persistence = new FilePersistence("./salve.dat");

    public static void setPersistence(IPersistence persistence) {
        MysqlContext.persistence = persistence;
    }

    public static IPersistence getPersistence() {
        return persistence;
    }

    public static <T> T get(Class<T> eventCls) {
        return objects.containsKey(eventCls) ? (T) objects.get(eventCls) : null;
    }

    public static void addRowCallback(RowCallback callback) {
        if (callback != null) {
            rowCallbacks.add(callback);
        }
    }

    public static void putTableCache(Map<? extends String, ? extends LinkedList<String>> m) {
        tablesCache.putAll(m);
    }

    public static LinkedList<RowCallback> getRowCallbacks() {
        return rowCallbacks;
    }

    public static <T> void add(T event) {
        objects.put(event.getClass(), event);
    }

    public static void addTable(TableMapEvent table) {
        String tableName = table.getSchemaName().concat(".").concat(table.getTableName());
        if (tablesCache.containsKey(tableName)) {
            LinkedList<String> list = tablesCache.get(tableName);
            table.setColumnInfo(list);
        }
        tables.put(table.getTableId(), table);
    }

    public static TableMapEvent getTable(Long id) {
        return tables.get(id);
    }
}
