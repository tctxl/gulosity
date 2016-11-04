package com.opdar.gulosity.replication.client;

import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.replication.base.StoreFileUtils;
import com.opdar.gulosity.utils.BufferUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 俊帆 on 2016/11/2.
 */
public class ReadRunnable implements Runnable {
    private final RowCallback rowCallback;
    private final SocketChannel channel;
    private long lastReadTime = 0l;
    private ExecutorService heartbeatEs = Executors.newCachedThreadPool();

    public ReadRunnable(SocketChannel channel, RowCallback rowCallback) {
        this.rowCallback = rowCallback;
        this.channel = channel;
    }

    @Override
    public void run() {
        lastReadTime = System.currentTimeMillis();
        heartbeatEs.execute(new ClientHeartbeat());
        while (channel.isConnected()) {
            int type = BufferUtils.readFixedData(channel, 1).get();
            switch (type) {
                case 1:{
                    ByteBuffer dst = BufferUtils.readFixedData(channel, 8, ByteOrder.BIG_ENDIAN);
                    long time = dst.getLong();
                    try {
                        //返回心跳
                        ByteArrayOutputStream barray = null;
                        DataOutputStream dataOutputStream = new DataOutputStream(barray = new ByteArrayOutputStream());
                        dataOutputStream.writeByte(1);
                        dataOutputStream.writeInt(8);
                        dataOutputStream.writeLong(lastReadTime = System.currentTimeMillis());
                        ByteBuffer buff = ByteBuffer.wrap(barray.toByteArray());
                        channel.write(buff);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2: {
                    //read log
                    ByteBuffer dst = BufferUtils.readFixedData(channel, 12, ByteOrder.BIG_ENDIAN);
                    long nextPosition = dst.getLong();
                    int bodyLength = dst.getInt();
                    dst = BufferUtils.readFixedData(channel, bodyLength, ByteOrder.BIG_ENDIAN);
                    StoreFileUtils.parseRow(dst, rowCallback);
                    break;
                }
                case 3:{
                    ByteBuffer dst = BufferUtils.readFixedData(channel, 8, ByteOrder.BIG_ENDIAN);
                    long position = dst.getLong();
                    System.out.println(position);
                    break;
                }
            }
        }
    }

    private class ClientHeartbeat implements Runnable{

        @Override
        public void run() {
            long sleepTime = 60;
            while (channel.isConnected()){
                long sec = (System.currentTimeMillis() - lastReadTime)/1000;
                if(sec > 60){
                    //overtime,downline client
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    //continue execute
                    sleepTime = 120 - sec;
                }
                try {
                    Thread.sleep(sleepTime*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
