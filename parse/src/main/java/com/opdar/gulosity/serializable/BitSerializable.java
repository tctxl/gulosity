package com.opdar.gulosity.serializable;

import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/27.
 */
public class BitSerializable extends JavaSerializable<Long> {
    public BitSerializable(int type) {
        super(type);
    }

    @Override
    public Long getValue(int meta, ByteBuffer buffer) {
        int nbits = ((meta >> 8) * 8) + (meta & 0xff);
        int len = (nbits + 7) / 8;
        long value = 0;
        if (nbits > 1) {
            byte[] result = BufferUtils.readFixedData(buffer, len);
            value = BufferUtils.readBELog(result, len);
        } else {
            value = buffer.get();
        }
        return value;
    }
}
