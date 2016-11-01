package com.opdar.gulosity.replication.server;

import com.opdar.gulosity.replication.server.base.Initializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultPromise;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class TcpServer {
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
    private ChannelFuture channelFuture;

    public void start(int port) throws InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        ChannelHandler initializer = new Initializer();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(initializer);
        channelFuture = b.bind(port).sync().channel().closeFuture().sync();
    }

    public boolean close() {
        if (channelFuture instanceof DefaultPromise) {
            ((DefaultPromise) channelFuture).setUncancellable();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        return true;
    }

    public static void main(String[] args){
        TcpServer tcpServer = new TcpServer();
        int testPort = 12034;
        try {
            tcpServer.start(testPort);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
