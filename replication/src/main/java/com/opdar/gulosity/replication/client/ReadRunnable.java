package com.opdar.gulosity.replication.client;

import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.replication.base.StoreFileUtils;
import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

/**
 * Created by 俊帆 on 2016/11/2.
 */
public class ReadRunnable implements Runnable {
    private final RowCallback rowCallback;
    private final SocketChannel channel;

    /**
     * Creates a DataInputStream that uses the specified
     * underlying InputStream.
     *
     * @param channel
     * @param rowCallback
     */
    public ReadRunnable(SocketChannel channel, RowCallback rowCallback) {
        this.rowCallback = rowCallback;
        this.channel = channel;
    }

    @Override
    public void run() {
        while (channel.isConnected()) {
            int type = BufferUtils.readFixedData(channel, 1).get();
            switch (type) {
                case 2: {
                    //read log
                    ByteBuffer dst = BufferUtils.readFixedData(channel, 8, ByteOrder.BIG_ENDIAN);
                    int nextPosition = dst.getInt();
                    int bodyLength = dst.getInt();
                    dst = BufferUtils.readFixedData(channel, bodyLength, ByteOrder.BIG_ENDIAN);
                    StoreFileUtils.parseRow(dst, rowCallback);
                    break;
                }
                case 3:
                    ByteBuffer dst = BufferUtils.readFixedData(channel, 4, ByteOrder.BIG_ENDIAN);
                    int position = dst.getInt();
                    System.out.println(position);
                    break;
            }
            System.out.println(type);
        }
    }
}
