package com.opdar.gulosity.serializable;

import java.nio.ByteBuffer;

/**
 * Created by 俊帆 on 2016/10/12.
 */
public class YearSerializable extends JavaSerializable<String> {
    public YearSerializable(int type) {
        super(type);
    }

    public String getValue(int meta, ByteBuffer buffer) {
        final int year = buffer.get();
        if (year == 0) {
            return  "0000";
        } else {
            return String.valueOf((short) (year + 1900));
        }
    }
}
