package com.opdar.gulosity.serializable;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

/**
 * Created by Shey on 2016/8/27.
 */
public class DateTimeSerializable extends JavaSerializable<String> {

    public DateTimeSerializable(int type) {
        super(type);
    }

    @Override
    public String getValue(int meta, ByteBuffer buffer) {

        final long i64 = buffer.getLong(); /* YYYYMMDDhhmmss */
        if (i64 == 0) {
            return "0000-00-00 00:00:00";
        } else {
            final int d = (int) (i64 / 1000000);
            final int t = (int) (i64 % 1000000);
            // if (cal == null) cal = Calendar.getInstance();
            // cal.clear();
                    /* month is 0-based, 0 for january. */
            // cal.set(d / 10000, (d % 10000) / 100 - 1, d % 100, t /
            // 10000, (t % 10000) / 100, t % 100);
            // value = new Timestamp(cal.getTimeInMillis());
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
