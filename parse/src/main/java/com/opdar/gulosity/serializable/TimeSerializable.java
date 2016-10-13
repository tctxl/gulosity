package com.opdar.gulosity.serializable;

import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by 俊帆 on 2016/10/12.
 */
public class TimeSerializable extends JavaSerializable<String> {
    public TimeSerializable(int type) {
        super(type);
    }

    public String getValue(int meta, ByteBuffer buffer) {

        final int i32 = (int) BufferUtils.readLong(buffer,3);
        final int u32 = Math.abs(i32);
        if (i32 == 0) {
            return  "00:00:00";
        } else {
            return String.format("%s%02d:%02d:%02d",
                    (i32 >= 0) ? "" : "-",
                    u32 / 10000,
                    (u32 % 10000) / 100,
                    u32 % 100);
        }
    }
}
