package com.opdar.gulosity.entity;

import com.opdar.gulosity.event.binlog.RowsEvent;

import java.util.Arrays;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class RowEntity {
    private String tableName;
    private String schemaName;
    private long tableId;
    private RowsEvent.Type eventType;
    private Object[] result;

    public RowEntity(int columnNum, RowsEvent.Type eventType, String schemaName,String tableName, long tableId) {
        result = new Object[columnNum];
        this.eventType = eventType;
        this.tableName = tableName;
        this.tableId = tableId;
        this.schemaName = schemaName;
    }

    public void set(int index, Object o) {
        result[index] = o;
    }

    public Object get(int index) {
        return result[index];
    }
    public Object[] getAll() {
        return result;
    }

    @Override
    public String toString() {
        return schemaName+"."+tableName+":"+Arrays.toString(result);
    }

    public RowsEvent.Type getEventType() {
        return eventType;
    }
}
