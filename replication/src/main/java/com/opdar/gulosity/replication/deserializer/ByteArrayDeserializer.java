package com.opdar.gulosity.replication.deserializer;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class ByteArrayDeserializer extends JavaDeserializer<byte[]> {
    public ByteArrayDeserializer(int type) {
        super(type);
    }

    @Override
    public byte[] getValue(byte[] buffer) {
        return buffer;
    }
}
