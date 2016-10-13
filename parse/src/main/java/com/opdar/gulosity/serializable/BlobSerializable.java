package com.opdar.gulosity.serializable;

import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.sql.Types;

/**
 * Created by 俊帆 on 2016/10/12.
 */
public class BlobSerializable extends JavaSerializable<ByteBuffer> {
    public BlobSerializable(int type) {
        super(type);
    }

    public ByteBuffer getValue(int meta, ByteBuffer buffer) {
        int len = 0;
        switch (meta) {
            case 1: {
                len = buffer.get();
                break;
            }
            case 2: {
                len = buffer.getShort();
                break;
            }
            case 3: {
                len = (0xff & buffer.get()) | ((0xff & buffer.get()) << 8) | ((0xff & buffer.get()) << 16);
                break;
            }
            case 4: {
                len = buffer.getInt();
                break;
            }
        }

        if(len > 0){
            byte[] binary = new byte[len];
            buffer.get(binary);
            return ByteBuffer.wrap(binary);
        }
        return null;
    }
}
