package com.opdar.gulosity.event.binlog;

import com.opdar.gulosity.base.Constants;
import com.opdar.gulosity.event.base.ChannelEvent;
import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by 俊帆 on 2016/10/18.
 */
public class XidEvent extends ChannelEvent {
    public XidEvent(BinlogHeader header, SocketChannel channel) {
        super(header, channel);
    }

    @Override
    public void doing() {
        ByteBuffer buffer = BufferUtils.readFixedData(getChannel(), (int) (getHeader().getEventLength() - Constants.MYSQL.FIXED_EVENT_LENGTH));
        long xid = buffer.getLong();
        System.out.println(xid);
    }
}
