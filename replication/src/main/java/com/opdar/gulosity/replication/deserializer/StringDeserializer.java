package com.opdar.gulosity.replication.deserializer;

import java.nio.ByteBuffer;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class StringDeserializer extends JavaDeserializer<String> {
    public StringDeserializer(int type) {
        super(type);
    }

    @Override
    public String getValue(byte[] buffer) {
        return new String(buffer);
    }
}
