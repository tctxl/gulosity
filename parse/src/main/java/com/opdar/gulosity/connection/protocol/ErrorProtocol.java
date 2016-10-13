package com.opdar.gulosity.connection.protocol;

import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;

public class ErrorProtocol {

    public byte fieldCount;
    public int errorNumber;
    public byte sqlStateMarker;
    public byte[] sqlState = new byte[5];
    public String message;

    /**
     * <pre>
     * VERSION 4.1
     *  Bytes                       Name
     *  -----                       ----
     *  1                           field_count, always = 0xff
     *  2                           errno
     *  1                           (sqlstate marker), always '#'
     *  5                           sqlstate (5 characters)
     *  n                           message
     *
     * </pre>
     */
    public void fromBytes(ByteBuffer buffer) {
        fieldCount = buffer.get();
        errorNumber = buffer.getShort();
        sqlStateMarker = buffer.get();
        buffer.get(sqlState);
        byte[] message = new byte[buffer.limit() - buffer.position()];
        buffer.get(message);
        this.message = new String(message);
    }

    public byte[] toBytes() {
        return null;
    }

    @Override
    public String toString() {
        return "ErrorProtocol [errorNumber=" + errorNumber + ", fieldCount=" + fieldCount + ", message=" + message
                + ", sqlState=" + sqlStateToString() + ", sqlStateMarker=" + (char) sqlStateMarker + "]";
    }

    private String sqlStateToString() {
        StringBuilder builder = new StringBuilder(5);
        for (byte b : this.sqlState) {
            builder.append((char) b);
        }
        return builder.toString();
    }

}
