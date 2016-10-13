package com.opdar.gulosity.serializable;

import com.opdar.gulosity.event.base.BinlogEvent;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shey on 2016/8/27.
 */
public abstract class JavaSerializable<T> {
    protected final int type;
    public JavaSerializable(int type) {
        this.type = type;
    }

    private static Map<Integer,Class<? extends JavaSerializable>> types = new HashMap<Integer, Class<? extends JavaSerializable>>(){
        {
            put(BinlogEvent.DATE,DateSerializable.class);
            put(BinlogEvent.NEWDATE,DateSerializable.class);
            put(BinlogEvent.TIME2,Time2Serializable.class);
            put(BinlogEvent.TIME,TimeSerializable.class);
            put(BinlogEvent.DATETIME,DateTimeSerializable.class);
            put(BinlogEvent.DATETIME2,DateTime2Serializable.class);
            put(BinlogEvent.TIMESTAMP,TimestampSerializable.class);
            put(BinlogEvent.TIMESTAMP2,TimestampSerializable.class);
            put(BinlogEvent.YEAR,YearSerializable.class);

            put(BinlogEvent.BLOB,BlobSerializable.class);
            put(BinlogEvent.VAR_STRING,VarcharSerializable.class);
            put(BinlogEvent.STRING,StringSerializable.class);
            put(BinlogEvent.VARCHAR,VarcharSerializable.class);
            put(BinlogEvent.INT24,NumberSerializable.class);
            put(BinlogEvent.LONG,NumberSerializable.class);
            put(BinlogEvent.LONGLONG,NumberSerializable.class);
            put(BinlogEvent.TINY,NumberSerializable.class);
            put(BinlogEvent.SHORT,NumberSerializable.class);
            put(BinlogEvent.FLOAT,NumberSerializable.class);
            put(BinlogEvent.DOUBLE,NumberSerializable.class);
            put(BinlogEvent.NEWDECIMAL,DecimalSerializable.class);

            put(BinlogEvent.BIT,BitSerializable.class);
            put(BinlogEvent.GEOMETRY,GeometrySerializable.class);

        }
    };

    public static JavaSerializable get(Integer key) {
        try {
            return types.get(key).getConstructor(int.class).newInstance(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract T getValue(int meta,ByteBuffer buffer);
}
