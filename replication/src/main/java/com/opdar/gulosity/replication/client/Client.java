package com.opdar.gulosity.replication.client;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class Client {
    public static void main(String[] args) {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.connect(new InetSocketAddress("localhost",12034));
            ByteArrayOutputStream barray = null;
            DataOutputStream dataOutputStream = new DataOutputStream(barray = new ByteArrayOutputStream());
            dataOutputStream.writeInt(12);
            dataOutputStream.write(new byte[]{1,2,3,4,5,6,7,8,9,0,1,2});
            ByteBuffer buff = ByteBuffer.wrap(barray.toByteArray());
            channel.write(buff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
