package com.opdar.gulosity.replication.server.base;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by 俊帆 on 2016/1/7.
 */
public abstract class HeartbeatInterval implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger("Heartbeat");
    private ChannelHandlerContext ctx;
    public HeartbeatInterval(ChannelHandlerContext ctx) {
        logger.info("Start heartbeat!");
        this.ctx = ctx;
    }

    public int overtime = 300;
    private long heartTime = 0;
    private boolean isOverTime = false;


    public void clearHeartbeat() {
        heartTime = System.currentTimeMillis();
        isOverTime = false;
    }

    public void start(){
        if(heartTime == 0){
            ctx.executor().schedule(this,overtime,TimeUnit.SECONDS);
        }else{
            clearHeartbeat();
        }
    }

    public void setOvertime(int overtime) {
        this.overtime = overtime;
    }

    @Override
    public void run() {
        if (!ctx.channel().isOpen()) {
            return;
        }
        long sec = (System.currentTimeMillis() - heartTime) / 1000;
        if (sec < 0) sec = overtime;
        if (sec < overtime) {
            sec = overtime - sec;
        } else {
            sec = overtime;
            if (isOverTime) {
                //超时
                overtime();
                return;
            } else {
                //正常
                heartbeat();
                isOverTime = true;
            }
        }
        ctx.executor().schedule(this, sec, TimeUnit.SECONDS);
    }

    public abstract void overtime();

    public abstract void heartbeat();
}
