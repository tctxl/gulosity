package com.opdar.gulosity.entity;

import com.opdar.gulosity.event.binlog.RowsEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class RowEntity {
    private final LinkedList<String> columnInfo;
    private String tableName;
    private String schemaName;
    private long tableId;
    private RowsEvent.Type eventType;
    private Object[] result;
    private int[] resultType;

    public RowEntity(int columnNum, RowsEvent.Type eventType, String schemaName, String tableName, LinkedList<String> columnInfo, long tableId) {
        result = new Object[columnNum];
        resultType = new int[columnNum];
        this.eventType = eventType;
        this.tableName = tableName;
        this.tableId = tableId;
        this.schemaName = schemaName;
        this.columnInfo = columnInfo;
    }

    public Object[] getResult() {
        return result;
    }

    public List<? extends Object> getColumnInfo() {
        return columnInfo;
    }

    public int[] getResultType() {
        return resultType;
    }

    public long getTableId() {
        return tableId;
    }

    public String getTableName() {
        return schemaName+"."+tableName;
    }

    public void set(int index, int type, Object value) {
        result[index] = value;
        resultType[index] = type;
    }

    public Object get(int index) {
        return result[index];
    }

    public Object[] getAll() {
        return result;
    }

    @Override
    public String toString() {
        if (columnInfo != null) {
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            for (int i = 0; i < result.length; i++) {
                builder.append(columnInfo.get(i)).append("=").append(result[i]).append(",");
            }
            builder.deleteCharAt(builder.length()-1);
            builder.append("}");
            return schemaName + "." + tableName + ":" + builder.toString();
        }
        return schemaName + "." + tableName + ":" + Arrays.toString(result);
    }

    public RowsEvent.Type getEventType() {
        return eventType;
    }
}
