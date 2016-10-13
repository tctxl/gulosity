package com.opdar.gulosity.event.binlog;

import com.opdar.gulosity.base.Constants;
import com.opdar.gulosity.event.base.ChannelEvent;
import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * binlog查询
 * Created by Shey on 2016/8/25.
 */
public class QueryEvent extends ChannelEvent {
    private int slaveProxyId;
    private int executionTime;
    private int schemaLength;
    private short errorCode;
    private short statusVarsLength;

    public QueryEvent(BinlogHeader header, SocketChannel channel) {
        super(header, channel);
    }

    public void doing() {
        ByteBuffer buffer = BufferUtils.readFixedData(getChannel(), (int) (getHeader().getEventLength() - Constants.MYSQL.FIXED_EVENT_LENGTH));
        slaveProxyId = buffer.getInt();
        executionTime = buffer.getInt();
        schemaLength = buffer.get();
        errorCode = buffer.getShort();
        if(getFormat().getBinlogVersion() >= 4){
            statusVarsLength = buffer.getShort();
        }
    }

    public int getSlaveProxyId() {
        return slaveProxyId;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public int getSchemaLength() {
        return schemaLength;
    }

    public short getErrorCode() {
        return errorCode;
    }

    public short getStatusVarsLength() {
        return statusVarsLength;
    }
}
