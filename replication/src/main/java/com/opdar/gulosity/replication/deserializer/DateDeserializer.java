package com.opdar.gulosity.replication.deserializer;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class DateDeserializer extends JavaDeserializer<Date> {
    public DateDeserializer(int type) {
        super(type);
    }

    @Override
    public Date getValue(byte[] buffer) {
        return Timestamp.valueOf(new String(buffer));
    }
}
