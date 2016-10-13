package com.opdar.gulosity.serializable;

import com.opdar.gulosity.event.base.BinlogEvent;
import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/27.
 */
public class StringSerializable extends JavaSerializable<String> {
    public StringSerializable(int type) {
        super(type);
    }

    @Override
    public String getValue(int meta, ByteBuffer buffer) {
        int len = 0;
        int type = 0;

        if (meta != 255) {
            type = meta >> 8;
            len = meta & 0xff;
            if ((type & 0x30) != 0x30) {
                    /* a long CHAR() field: see #37426 */
                len = len | (((type & 0x30) ^ 0x30) << 4);
                type = type | 0x30;
            }
        }

        switch (type & 0xff) {
            case BinlogEvent.SET: {
                final int nbits = (meta & 0xFF) * 8;
                len = (nbits + 7) / 8;
                Long value = null;
                if (nbits > 1) {
                    value = BufferUtils.readLong(buffer, len);
                } else {
                    value = (long) buffer.get();
                }
                return String.valueOf(value);
            }
            case BinlogEvent.ENUM: {
                return String.valueOf(BufferUtils.readLong(buffer, len));
            }
            default:
                if (len < 256) {
                    len = buffer.get();
                } else {
                    len = buffer.getShort();
                }
                return BufferUtils.readFixedString(buffer, len);
        }
    }
}
