package com.opdar.gulosity.replication.server.base;

import com.opdar.gulosity.replication.base.Registry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class IoSession {
    private final ChannelHandlerContext ctx;
    private String uid;
    private HeartbeatInterval heartbeat=null;

    public IoSession(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void initHeartbeat(){
        heartbeat = new HeartbeatInterval(ctx) {
            @Override
            public void overtime() {
                IoSession.this.downline();
            }

            @Override
            public void heartbeat() {
                IoSession.this.heartbeat();
            }
        };
        heartbeat.setOvertime(5);
        heartbeat.start();
    }

    public void downline() {
        Registry.remove(uid);
        ctx.close();
    }

    public void heartbeat() {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        //type
        buffer.put((byte)1);
        buffer.putLong(System.currentTimeMillis());
        ctx.writeAndFlush(buffer.array()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {

                }
            }
        });
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

    public void writePos() {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(Registry.FILE_PATH, "r");
            int size = (int) randomAccessFile.getChannel().size();
            writePos(size);
        } catch (Exception ignored) {}finally {
            if(randomAccessFile != null) try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writePos(int position) {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        //type
        buffer.put((byte)3);
        buffer.putInt(position);
        ctx.writeAndFlush(buffer.array()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    Registry.put(uid,IoSession.this);
                }
            }
        });
    }

    public void setUid(String uid) {
        this.uid = uid;
        initHeartbeat();
    }

    public String getUid() {
        return uid;
    }

    public HeartbeatInterval getHeartbeat() {
        return heartbeat;
    }
}
