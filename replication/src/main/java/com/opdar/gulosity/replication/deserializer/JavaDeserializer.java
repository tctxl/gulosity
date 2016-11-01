package com.opdar.gulosity.replication.deserializer;

import com.opdar.gulosity.event.base.BinlogEvent;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shey on 2016/8/27.
 */
public abstract class JavaDeserializer<T> {
    protected final int type;

    public JavaDeserializer(int type) {
        this.type = type;
    }

    private static Map<Integer, Class<? extends JavaDeserializer>> types = new HashMap<Integer, Class<? extends JavaDeserializer>>() {
        {
            put(BinlogEvent.DATE, DateDeserializer.class);
            put(BinlogEvent.NEWDATE, DateDeserializer.class);
            put(BinlogEvent.TIME2, StringDeserializer.class);
            put(BinlogEvent.TIME, StringDeserializer.class);
            put(BinlogEvent.DATETIME, StringDeserializer.class);
            put(BinlogEvent.DATETIME2, StringDeserializer.class);
            put(BinlogEvent.TIMESTAMP, TimestampDeserializer.class);
            put(BinlogEvent.TIMESTAMP2, TimestampDeserializer.class);
            put(BinlogEvent.YEAR, StringDeserializer.class);
            put(BinlogEvent.VAR_STRING, StringDeserializer.class);
            put(BinlogEvent.STRING, StringDeserializer.class);
            put(BinlogEvent.VARCHAR, StringDeserializer.class);
            put(BinlogEvent.INT24, IntegerDeserializer.class);
            put(BinlogEvent.LONG, LongDeserializer.class);
            put(BinlogEvent.LONGLONG, LongDeserializer.class);
            put(BinlogEvent.TINY, IntegerDeserializer.class);
            put(BinlogEvent.SHORT, IntegerDeserializer.class);
            put(BinlogEvent.FLOAT, FloatDeserializer.class);
            put(BinlogEvent.DOUBLE, DoubleDeserializer.class);
            put(BinlogEvent.BIT, LongDeserializer.class);
            put(BinlogEvent.NEWDECIMAL, DecimalDeserializer.class);

            put(BinlogEvent.BLOB, ByteBufferDeserializer.class);
            put(BinlogEvent.GEOMETRY, ByteArrayDeserializer.class);

        }
    };

    public static JavaDeserializer get(Integer key) {
        try {
            return types.get(key).getConstructor(int.class).newInstance(key);
        } catch (Exception e) {
            throw new RuntimeException("key is "+key,e);
        }
    }

    public abstract T getValue(byte[] buffer);
}
