package com.opdar.gulosity.event.binlog;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.event.base.ChannelEvent;
import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Shey on 2016/8/22.
 */
public class RotateEvent extends ChannelEvent {
    private String fileName;
    private long position;

    public RotateEvent(BinlogHeader header, SocketChannel channel) {
        super(header, channel);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public void doing() {
        position = BufferUtils.readFixedData(getChannel(), 8).getLong();
        int length = (int) (getHeader().getEventLength() - 8 - 19);
        ByteBuffer dst2 = BufferUtils.readFixedData(getChannel(), length);
        fileName = new String(dst2.array(), 0, length);
        MysqlContext.getPersistence().commit(position);
        MysqlContext.getPersistence().setFileName(fileName);
    }
}
