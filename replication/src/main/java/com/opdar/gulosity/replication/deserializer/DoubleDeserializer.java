package com.opdar.gulosity.replication.deserializer;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class DoubleDeserializer extends JavaDeserializer<Double> {
    public DoubleDeserializer(int type) {
        super(type);
    }

    @Override
    public Double getValue(byte[] buffer) {
        return new Double(new String(buffer));
    }
}
