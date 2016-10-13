package com.opdar.gulosity.serializable;

import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/27.
 */
public class VarcharSerializable extends JavaSerializable<String> {
    public VarcharSerializable(int type) {
        super(type);
    }

    @Override
    public String getValue(int meta, ByteBuffer buffer) {
        int len;
        if (meta < 256) {
            len = buffer.get();
        } else {
            len = buffer.getShort();
        }
        return BufferUtils.readFixedString(buffer, len);
    }
}
