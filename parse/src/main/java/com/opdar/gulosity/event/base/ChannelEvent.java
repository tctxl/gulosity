package com.opdar.gulosity.event.base;

import com.opdar.gulosity.event.EventType;
import com.opdar.gulosity.event.binlog.BinlogHeader;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by Shey on 2016/8/22.
 */
public abstract class ChannelEvent extends BinlogEvent {
    private SocketChannel channel;

    private ChannelEvent prev;

    public ChannelEvent(BinlogHeader header, SocketChannel channel) {
        super(header);
        this.channel = channel;
    }

    public void setPrev(ChannelEvent prev) {
        this.prev = prev;
    }

    public ChannelEvent getPrev() {
        return prev;
    }

    public void next() {
        try {
            Event event = EventType.get(getChannel(), this);
            event.doing();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }
}
