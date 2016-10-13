package com.opdar.gulosity.serializable;

import java.nio.ByteBuffer;
import java.sql.Timestamp;

/**
 * Created by Shey on 2016/8/27.
 */
public class TimestampSerializable extends JavaSerializable<Timestamp> {

    public TimestampSerializable(int type) {
        super(type);
    }

    @Override
    public Timestamp getValue(int meta, ByteBuffer buffer) {

        final long i32 = buffer.getInt();
        if (i32 == 0) {
            return Timestamp.valueOf("0000-00-00 00:00:00");
        } else {
            return new Timestamp(i32 * 1000);
        }
    }
}
