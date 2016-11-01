package com.opdar.gulosity.replication.deserializer;

import java.math.BigDecimal;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class DecimalDeserializer extends JavaDeserializer<BigDecimal> {
    public DecimalDeserializer(int type) {
        super(type);
    }

    @Override
    public BigDecimal getValue(byte[] buffer) {
        return new BigDecimal(new String(buffer));
    }
}
