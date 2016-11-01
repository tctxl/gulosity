package com.opdar.gulosity.replication.server.base;

import com.opdar.gulosity.replication.server.protocol.Heartbeat;
import com.opdar.gulosity.replication.server.protocol.RequestLog;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Sharable
public class Handler extends SimpleChannelInboundHandler<Object> {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    public static AttributeKey<IoSession> SESSION_FLAG = AttributeKey.valueOf("session");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof IOException)) {
            cause.printStackTrace();
            logger.error(cause.getMessage());
            ctx.close();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        IoSession session = ctx.attr(SESSION_FLAG).get();
        session.downline();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ctx.attr(SESSION_FLAG).set(new IoSession(ctx));
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object result) throws Exception {
        IoSession session = ctx.attr(SESSION_FLAG).get();
        if (result instanceof Heartbeat) {
            session.heartbeat();
        } else if (result instanceof RequestLog) {
            //主动请求Log
            byte[] ready = ((RequestLog) result).read();
            int nextPosition = ((RequestLog) result).getPosition() + ready.length;
            session.writeLog(nextPosition, ready);
        }
    }

}
