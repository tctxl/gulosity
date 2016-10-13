package com.opdar.gulosity.event.binlog;

import com.opdar.gulosity.base.Constants;
import com.opdar.gulosity.event.base.ChannelEvent;
import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Shey on 2016/8/25.
 */
public class FormatDescriptionEvent extends ChannelEvent {

    private int binlogVersion;
    private String serverVersion;
    private int createTimestamp;
    private int headerLength;
    private byte[] postHeader;

    public FormatDescriptionEvent(BinlogHeader header, SocketChannel channel) {
        super(header, channel);
    }


    /**
     * header (19 bytes)
     * binlog version (2 bytes)
     * server version (ST_SERVER_VER_LEN = 50 bytes)
     * timestamp (4 bytes)
     * Summing those lengths yields 19 + 2 + 50 + 4 = 75
     */
    public void doing() {
        ByteBuffer buffer = BufferUtils.readFixedData(getChannel(), (int) (getHeader().getEventLength() - Constants.MYSQL.FIXED_EVENT_LENGTH));

        this.binlogVersion = buffer.getShort();
        byte[] serverVersion = new byte[50];
        buffer.get(serverVersion);
        this.serverVersion = new String(serverVersion, 0, 50);
        this.createTimestamp = buffer.getInt();
        this.headerLength = buffer.get();
        this.postHeader = new byte[(int) (getHeader().getEventLength() - Constants.MYSQL.FIXED_EVENT_LENGTH - 57)];
        buffer.get(postHeader);
    }

    public int getBinlogVersion() {
        return binlogVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public int getCreateTimestamp() {
        return createTimestamp;
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public byte[] getPostHeader() {
        return postHeader;
    }
}
