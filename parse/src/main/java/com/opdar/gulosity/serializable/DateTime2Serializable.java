package com.opdar.gulosity.serializable;

import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

/**
 * Created by Shey on 2016/8/27.
 */
public class DateTime2Serializable extends JavaSerializable<String> {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public DateTime2Serializable(int type) {
        super(type);
    }

    @Override
    public String getValue(int meta, ByteBuffer buffer) {
        long intpart = BufferUtils.readBELog(buffer, 5) - 0x8000000000L; // big-endian
        int frac = 0;
        switch (meta) {
            case 0:
                frac = 0;
                break;
            case 1:
            case 2:
                frac = buffer.get() * 10000;
                break;
            case 3:
            case 4:
                frac = (int) (BufferUtils.readBELog(buffer, 2) * 100);
                break;
            case 5:
            case 6:
                frac = (int) BufferUtils.readBELog(buffer, 3);
                break;
            default:
                frac = 0;
                break;
        }

        String second = null;
        if (intpart == 0) {
            second = "0000-00-00 00:00:00";
        } else {
            long ymd = intpart >> 17;
            long ym = ymd >> 5;
            long hms = intpart % (1 << 17);
            second = String.format("%04d-%02d-%02d %02d:%02d:%02d",
                    (int) (ym / 13),
                    (int) (ym % 13),
                    (int) (ymd % (1 << 5)),
                    (int) (hms >> 12),
                    (int) ((hms >> 6) % (1 << 6)),
                    (int) (hms % (1 << 6)));
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
