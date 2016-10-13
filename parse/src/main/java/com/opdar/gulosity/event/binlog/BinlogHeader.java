package com.opdar.gulosity.event.binlog;

import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/24.
 */
public class BinlogHeader {
    private long timestamp;
    private int typeCode;
    private long serverId;
    private long eventLength;
    private long nextPosition;
    private long flags;
    private int checksumAlg;

    public BinlogHeader(ByteBuffer buffer) {
        timestamp = buffer.getInt();
        typeCode = 0xff & buffer.get();
        serverId = buffer.getInt();
        eventLength = buffer.getInt();
        nextPosition = buffer.getInt();
        flags = buffer.getShort();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public long getEventLength() {
        return eventLength;
    }

    public void setEventLength(long eventLength) {
        this.eventLength = eventLength;
    }

    public long getNextPosition() {
        return nextPosition;
    }

    public void setNextPosition(long nextPosition) {
        this.nextPosition = nextPosition;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public int getChecksumAlg() {
        return checksumAlg;
    }

    public void setChecksumAlg(int checksumAlg) {
        this.checksumAlg = checksumAlg;
    }
}
