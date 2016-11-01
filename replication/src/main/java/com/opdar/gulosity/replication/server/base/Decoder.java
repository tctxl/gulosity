package com.opdar.gulosity.replication.server.base;

import com.opdar.gulosity.replication.server.protocol.Heartbeat;
import com.opdar.gulosity.replication.server.protocol.RequestLog;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ReferenceCountUtil;

import java.net.SocketAddress;
import java.util.Arrays;

/**
 * Created by 俊帆 on 2015/8/27.
 */
@ChannelHandler.Sharable
public class Decoder extends ChannelInboundHandlerAdapter {

    public Decoder() {
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        try {
            ByteBuf cast = null;
            SocketAddress address = null;
            if (msg instanceof DatagramPacket) {
                cast = ((DatagramPacket) msg).copy().content();
                address = ((DatagramPacket) msg).sender();
            } else {
                cast = (ByteBuf) msg;
                address = ctx.channel().remoteAddress();
            }
            try {
                decode(ctx, cast, address);
            } finally {
                ReferenceCountUtil.release(cast);
            }
        } catch (DecoderException e) {
            throw e;
        } catch (Exception e) {
            throw new DecoderException(e);
        }
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, SocketAddress address) throws Exception {
        while (byteBuf.readableBytes() != 0) {
            if (byteBuf.isReadable(5)) {
                byteBuf.markReaderIndex();
                int type = byteBuf.readByte();
                int length = byteBuf.readInt();
                if (byteBuf.readableBytes() < length) {
                    byteBuf.resetReaderIndex();
                    break;
                }
                byte[] bytes = new byte[length];
                byteBuf.readBytes(bytes);
                switch (type){
                    case 1:
                        //heartbeat
                        //version|timestamp
                        Heartbeat heartbeat = new Heartbeat();
                        channelHandlerContext.fireChannelRead(heartbeat);
                        break;
                    case 2:
                        //start seek
                        RequestLog requestLog = new RequestLog();
                        channelHandlerContext.fireChannelRead(requestLog);
                        break;
                    default:
                        break;
                }
            }
        }
    }

}
