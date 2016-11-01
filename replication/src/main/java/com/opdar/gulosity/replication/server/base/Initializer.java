package com.opdar.gulosity.replication.server.base;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class Initializer extends ChannelInitializer<SocketChannel> {

    protected static final Decoder DECODER = new Decoder();
    protected static final Encoder ENCODER = new Encoder();
    protected static final Handler HANDLER = new Handler();
    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", DECODER);
        pipeline.addLast("encoder", ENCODER);
        pipeline.addLast("handler", HANDLER);
    }

}
