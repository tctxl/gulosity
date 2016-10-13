package com.opdar.gulosity.base;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Shey on 2016/8/23.
 */
public class CommandBuffer extends DataOutputStream {
    public CommandBuffer() {
        super(new ByteArrayOutputStream());
    }

    public ByteBuffer toBuffer() throws IOException {
        try {
            ByteArrayOutputStream outputStream = (ByteArrayOutputStream) this.out;
            return ByteBuffer.wrap(outputStream.toByteArray());
        }finally {
            close();
            this.out = new ByteArrayOutputStream();
        }
    }

    public void writeInt16(int data) {
        try {
            out.write((byte) (data & 0xFF));
            out.write((byte) ((data >>> 8) & 0xFF));
            out.write((byte) ((data >>> 16) & 0xFF));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
