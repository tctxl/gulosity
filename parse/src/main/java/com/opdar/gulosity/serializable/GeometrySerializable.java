package com.opdar.gulosity.serializable;

import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/27.
 */
public class GeometrySerializable extends JavaSerializable<byte[]> {
    public GeometrySerializable(int type) {
        super(type);
    }

    @Override
    public byte[] getValue(int meta, ByteBuffer buffer) {
        int len = (int) BufferUtils.readLong(buffer,meta);
        return BufferUtils.readFixedData(buffer,len);
    }
}
