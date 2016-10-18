package com.opdar.gulosity.serializable;

import com.opdar.gulosity.event.base.BinlogEvent;
import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/27.
 */
public class NumberSerializable extends JavaSerializable<Number> {

    public NumberSerializable(int type) {
        super(type);
    }

    @Override
    public Number getValue(int meta, ByteBuffer buffer) {
        if (type == BinlogEvent.TINY) {
            return buffer.get();
        }
        if (type == BinlogEvent.SHORT) {
            return buffer.getShort();
        }
        if (type == BinlogEvent.INT24) {
            return BufferUtils.readLong(buffer, 3);
        }
        if (type == BinlogEvent.LONGLONG) {
            return buffer.getLong();
        }
        if (type == BinlogEvent.FLOAT) {
            return buffer.getFloat();
        }
        if (type == BinlogEvent.DOUBLE) {
            return buffer.getDouble();
        }
        return buffer.getInt();
    }
}
