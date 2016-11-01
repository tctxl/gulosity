package com.opdar.gulosity.replication.server.base;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.nio.ByteBuffer;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class IoSession {
    private final ChannelHandlerContext ctx;

    public IoSession(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void downline() {

    }

    public void heartbeat() {

    }

    public void writeLog(int nextPosition, byte[] ready) {
        ByteBuffer buffer = ByteBuffer.allocate(9 + ready.length);
        //type
        buffer.put((byte)2);
        //seek pos
        buffer.putInt(nextPosition);
        //body length
        buffer.putInt(ready.length);
        //body...
        buffer.put(ready);
        ctx.writeAndFlush(buffer.array()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {

                }
            }
        });
    }
}
