package com.opdar.gulosity.replication.deserializer;

import java.nio.ByteBuffer;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class LongDeserializer extends JavaDeserializer<Long> {
    public LongDeserializer(int type) {
        super(type);
    }

    @Override
    public Long getValue(byte[] buffer) {
        return new Long(new String(buffer));
    }
}
