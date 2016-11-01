package com.opdar.gulosity.replication.deserializer;

import java.sql.Timestamp;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class TimestampDeserializer extends JavaDeserializer<Timestamp> {
    public TimestampDeserializer(int type) {
        super(type);
    }

    @Override
    public Timestamp getValue(byte[] buffer) {
        return Timestamp.valueOf(new String(buffer));
    }
}
