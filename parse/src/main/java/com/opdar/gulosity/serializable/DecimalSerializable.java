package com.opdar.gulosity.serializable;

import com.opdar.gulosity.utils.BufferUtils;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/27.
 */
public class DecimalSerializable extends JavaSerializable<BigDecimal> {
    private static final int dig2bytes[] = {0, 1, 1, 2, 2, 3, 3, 4, 4, 4};
    private static final int DIG_PER_DEC1 = 9;

    public DecimalSerializable(int type) {
        super(type);
    }

    @Override
    public BigDecimal getValue(int meta, ByteBuffer buffer) {
        int precision = meta >> 8;
        int scale = meta & 0xff;
        return bin2decimal(buffer, precision, scale);
    }

    private BigDecimal bin2decimal(ByteBuffer buffer, int precision, int scale) {
        int intg = precision - scale,
                intg0 = intg / DIG_PER_DEC1, frac0 = scale / DIG_PER_DEC1,
                intg0x = intg - intg0 * DIG_PER_DEC1, frac0x = scale - frac0 * DIG_PER_DEC1,
                intg1 = intg0 + (intg0x > 0 ? 1 : 0), frac1 = frac0 + (frac0x > 0 ? 1 : 0);
        byte m = buffer.get();
        boolean unsigned = (m & 0x80) == 0x80;

        int bin_size = intg0 * 4 + dig2bytes[intg0x] +
                frac0 * 4 + dig2bytes[frac0x];
        byte[] ucharBuf = BufferUtils.readFixedData(buffer, bin_size - 1);
        if (!unsigned) {
            for (int i = 0; i < ucharBuf.length; i++) {
                ucharBuf[i] ^= -1;
            }
        }
        byte[] dest = new byte[ucharBuf.length + 1];
        dest[0] = (byte) (((m ^ 0x80)) ^ (unsigned ? 0 : -1));
        System.arraycopy(ucharBuf, 0, dest, 1, dest.length-1);

        StringBuilder builder = new StringBuilder();
        int d = calculate(builder, intg0x, intg1, unsigned, dest);
        //计算小数点后面的数字
        if (scale > 0) {
            dest = new byte[bin_size-d];
            System.arraycopy(ucharBuf, d-1, dest, 0, dest.length    );
            calculate(builder.append("."), frac0x, frac1, true, dest);
        }
        if(!unsigned){
            builder.insert(0,'-');
        }
        return new BigDecimal(builder.toString());
    }

    private int calculate(StringBuilder builder, int intg0x, int intg1, boolean unsigned, byte[] ucharBuf) {
        int d = 0;
        int len = 0;
        byte[] dest = null;
        for (int i = 0; i < intg1; i++) {
            if(i == 0){
                len = intg0x == 0 ? 4 : dig2bytes[intg0x];
                dest = new byte[len];
                System.arraycopy(ucharBuf, 0, dest, 0, len);
            }else{
                len = 4;
                dest = new byte[len];
                System.arraycopy(ucharBuf, d, dest, 0, len);
            }
            d += len;
            int x = (int) BufferUtils.readBELog(dest, len);
            builder.append(x);
        }
        return d;
    }
}