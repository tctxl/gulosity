package com.opdar.gulosity.serializable;

import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/27.
 */
public class DateTimeSerializable extends JavaSerializable<String> {

    public DateTimeSerializable(int type) {
        super(type);
    }

    @Override
    public String getValue(int meta, ByteBuffer buffer) {

        final long i64 = buffer.getLong();
        if (i64 == 0) {
            return "0000-00-00 00:00:00";
        } else {
            final int d = (int) (i64 / 1000000);
            final int t = (int) (i64 % 1000000);
            return String.format("%04d-%02d-%02d %02d:%02d:%02d",
                    d / 10000,
                    (d % 10000) / 100,
                    d % 100,
                    t / 10000,
                    (t % 10000) / 100,
                    t % 100);
        }
    }
}
