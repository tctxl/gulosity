package com.opdar.gulosity.event.binlog;

import com.opdar.gulosity.connection.MysqlConnection;
import com.opdar.gulosity.event.base.Event;
import com.opdar.gulosity.event.EventType;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Shey on 2016/8/22.
 */
public class SalveFetchEvent implements Event {

    private final MysqlConnection connection;
    private AtomicBoolean running = new AtomicBoolean();

    public SalveFetchEvent(MysqlConnection connection) {
        this.connection = connection;
    }

    /**
     * v4版本
     */
    public void doing() {
        SocketChannel channel = connection.getChannel();
        if (running.compareAndSet(false, true)) {
                while (running.get()) {

                    try {
                        Event event = EventType.get(channel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}
