package com.opdar.gulosity.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.BitSet;

/**
 * Created by Shey on 2016/8/19.
 */
public class BufferUtils {

    public static void writeInt(long data, ByteArrayOutputStream out) {
        out.write((byte) (data & 0xFF));
        out.write((byte) (data >>> 8));
        out.write((byte) (data >>> 16));
        out.write((byte) (data >>> 24));
    }

    public static void writeInt8(int data, ByteArrayOutputStream out) {
        out.write((byte) (data & 0xFF));
        out.write((byte) ((data >>> 8) & 0xFF));
    }

    public static void writeInt16(int data, ByteArrayOutputStream out) {
        out.write((byte) (data & 0xFF));
        out.write((byte) ((data >>> 8) & 0xFF));
        out.write((byte) ((data >>> 16) & 0xFF));
    }

    public static void writeLength(byte[] data, ByteArrayOutputStream out) throws IOException {
        if (data.length < 0xfc) {
            out.write((byte) data.length);
        } else if (data.length < (1 << 16L)) {
            out.write((byte) 0xfc);
            writeInt8(data.length, out);
        } else if (data.length < (1 << 24L)) {
            out.write((byte) 0xfd);
            writeInt16(data.length, out);
        } else {
            out.write((byte) 0xfe);
            writeInt(data.length, out);
        }
        out.write(data);
    }

    public static byte[] readFixedData(ByteBuffer buffer, int length)  {
        byte[] bytes  = new byte[length];
        buffer.get(bytes);
        return bytes;
    }
    public static long readBELog(ByteBuffer buffer,int len){
        long value = 0;
        byte[] bytes = new byte[len];
        buffer.get(bytes);
        for(int i=len-1;i>=0;i--){
            value |= (bytes[i] & 0xff) << (8 * (len-i-1));
        }
        return value;
    }
    public static long readBELog(byte[] bytes,int len){
        long value = 0;
        for(int i=len-1;i>=0;i--){
            value |= (bytes[i] & 0xff) << (8 * (len-i-1));
        }
        return value;
    }
    public static ByteBuffer readFixedData(SocketChannel channel, int length)  {
        return readFixedData(channel,length,ByteOrder.LITTLE_ENDIAN);
    }


    public static String readFixedString(ByteBuffer buffer, int length,Charset charset)  {
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes,0,length,charset);
    }
    public static String readFixedString(ByteBuffer buffer, int length)  {
        return readFixedString(buffer, length, Charset.defaultCharset());
    }

    public static long readLong(byte[] buffer,int bit){
        long result = 0;
        for (int i = 0; i < bit; ++i) {
            int c = buffer[i];
            result |= ((c&0xff) <<(8*i));
        }
        return result;
    }

    public static long readLong(ByteBuffer buffer,int bit){
        long result = 0;
        for (int i = 0; i < bit; ++i) {
            int c = buffer.get();
            result |= ((c&0xff) <<(8*i));
        }
        return result;
    }

    public static ByteBuffer readFixedData(SocketChannel channel, int length,ByteOrder bo)  {
        try {
            ByteBuffer dst2 = ByteBuffer.wrap(new byte[length]);
            dst2.order(bo);
            channel.read(dst2);
            dst2.position(0);
            return dst2;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BitSet readBitmap(int columnCount, ByteBuffer buffer) {
        BitSet bitmap = new BitSet(columnCount);

        byte[] nullBitmapBytes = new byte[(columnCount + 7) / 8];
        buffer.get(nullBitmapBytes);
        for(int i=0;i<nullBitmapBytes.length;i++){
            int flag = nullBitmapBytes[i];
            if(flag == 0)continue;
            for(int j=0;j<8;j++){
                if(i*8+j>columnCount)break;
                if ((flag & 1<<j) != 0) bitmap.set(i*8+j);
            }
        }
        return bitmap;
    }
}
