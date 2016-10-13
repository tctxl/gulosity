package com.opdar.gulosity.serializable;

import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by 俊帆 on 2016/10/12.
 */
public class Time2Serializable extends JavaSerializable<String> {
    public Time2Serializable(int type) {
        super(type);
    }

    public String getValue(int meta, ByteBuffer buffer) {
        long intpart = 0;
        int frac = 0;
        long ltime = 0;
        switch (meta) {
            case 0:
                intpart = BufferUtils.readBELog(buffer, 3) - 0x8000000000L; // big-endian
                ltime = intpart << 24;
                break;
            case 1:
            case 2:
                intpart = BufferUtils.readBELog(buffer, 3) - 0x8000000000L;
                frac = buffer.get();
                if (intpart < 0 && frac > 0) {
                    intpart++; /* Shift to the next integer value */
                    frac -= 0x100; /* -(0x100 - frac) */
                    // fraclong = frac * 10000;
                }
                frac = frac * 10000;
                ltime = intpart << 24;
                break;
            case 3:
            case 4:
                intpart = BufferUtils.readBELog(buffer, 3) - 0x8000000000L;
                frac = (int) BufferUtils.readBELog(buffer, 2);
                if (intpart < 0 && frac > 0) {
                            /*
                             * Fix reverse fractional part order:
                             * "0x10000 - frac". See comments for FSP=1 and
                             * FSP=2 above.
                             */
                    intpart++; /* Shift to the next integer value */
                    frac -= 0x10000; /* -(0x10000-frac) */
                    // fraclong = frac * 100;
                }
                frac = frac * 100;
                ltime = intpart << 24;
                break;
            case 5:
            case 6:
                intpart = BufferUtils.readBELog(buffer, 6) - 0x800000000000L;
                ltime = intpart;
                frac = (int) (intpart % (1L << 24));
                break;
            default:
                intpart = BufferUtils.readBELog(buffer, 3) - 0x800000L;
                ltime = intpart << 24;
                break;
        }

        String second = null;
        if (intpart == 0) {
            second = "00:00:00";
        } else {
            long ultime = Math.abs(ltime);
            intpart = ultime >> 24;
            second = String.format("%s%02d:%02d:%02d",
                    ltime >= 0 ? "" : "-",
                    (int) ((intpart >> 12) % (1 << 10)),
                    (int) ((intpart >> 6) % (1 << 6)),
                    (int) (intpart % (1 << 6)));
        }

        if (meta >= 1) {
            String sec = String.valueOf(frac);

            if (sec.length() < 6) {
                StringBuilder result = new StringBuilder(6);
                int len = 6 - sec.length();
                for (; len > 0; len--) {
                    result.append('0');
                }
                result.append(sec);
                sec = result.toString();
            }

            String microSecond = sec.substring(0, meta);
            return second + '.' + microSecond;
        } else {
            return second;
        }
    }
}
