package com.opdar.gulosity.connection.protocol;
/**
 * Created by Shey on 2016/8/19.
 */
public class HeaderProtocol implements Protocol {
    private int  bodyLength;
    private byte sequence;
    /**
       Up to MySQL 3.22, 0xfe was followed by a 4-byte integer.
     */
    public void fromBytes(byte[] data) {
        this.bodyLength = (data[0] & 0xFF) | ((data[1] & 0xFF) << 8) | ((data[2] & 0xFF) << 16);
        sequence = data[3];
    }

    public byte[] toBytes() {
        return new byte[]{(byte) (bodyLength & 0xFF),(byte) (bodyLength >>> 8),(byte) (bodyLength >>> 16),sequence};
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public byte getSequence() {
        return sequence;
    }

    public void setSequence(byte sequence) {
        this.sequence = sequence;
    }
}
