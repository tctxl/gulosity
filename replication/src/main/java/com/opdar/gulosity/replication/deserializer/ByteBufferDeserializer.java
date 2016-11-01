package com.opdar.gulosity.replication.deserializer;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class ByteBufferDeserializer extends JavaDeserializer<ByteBuffer> {
    public ByteBufferDeserializer(int type) {
        super(type);
    }

    @Override
    public ByteBuffer getValue(byte[] buffer) {
        return ByteBuffer.wrap(buffer);
    }
}
