package com.opdar.gulosity.replication.deserializer;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class FloatDeserializer extends JavaDeserializer<Float> {
    public FloatDeserializer(int type) {
        super(type);
    }

    @Override
    public Float getValue(byte[] buffer) {
        return new Float(new String(buffer));
    }
}
