package com.opdar.gulosity.replication.deserializer;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class IntegerDeserializer extends JavaDeserializer<Integer> {
    public IntegerDeserializer(int type) {
        super(type);
    }

    @Override
    public Integer getValue(byte[] buffer) {
        return new Integer(new String(buffer));
    }
}
