package com.opdar.gulosity.event.base;

import com.opdar.gulosity.connection.entity.Column;
import com.opdar.gulosity.utils.MysqlUtils;

import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;

/**
 * Created by 俊帆 on 2016/10/18.
 */
public class HeartbeatEvent implements Event {
    private SocketChannel channel;

    public HeartbeatEvent(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void doing() {
        List<Map<Column, String>> list = MysqlUtils.query(channel, "SELECT 1");
        System.out.println(list);
    }
}
