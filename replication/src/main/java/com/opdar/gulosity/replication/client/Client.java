package com.opdar.gulosity.replication.client;

import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.replication.base.StoreFileUtils;
import com.opdar.gulosity.utils.BufferUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class Client implements RowCallback{
    SocketChannel channel;
    public static void main(String[] args) {
        try {
            Client client = new Client().open(new InetSocketAddress("localhost", 12034));
            client.requestPos();
            client.requestLog(14104);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client open(InetSocketAddress address) throws IOException {
        channel = SocketChannel.open();
        channel.connect(address);
        return this;
    }

    public void requestLog(int seek) throws IOException {
        ByteArrayOutputStream barray = null;
        DataOutputStream dataOutputStream = new DataOutputStream(barray = new ByteArrayOutputStream());
        dataOutputStream.writeByte(2);
        dataOutputStream.writeInt(4);
        dataOutputStream.writeInt(seek);
        ByteBuffer buff = ByteBuffer.wrap(barray.toByteArray());
        channel.write(buff);
        ByteBuffer dst = BufferUtils.readFixedData(channel,9, ByteOrder.BIG_ENDIAN);
        int type = dst.get();
        int nextPosition = dst.getInt();
        System.out.println("next position is "+nextPosition);
        int bodyLength = dst.getInt();
        dst = BufferUtils.readFixedData(channel,bodyLength, ByteOrder.BIG_ENDIAN);
        StoreFileUtils.parseRow(dst,this);
    }

    public void requestPos() throws IOException {
        ByteArrayOutputStream barray = null;
        DataOutputStream dataOutputStream = new DataOutputStream(barray = new ByteArrayOutputStream());
        dataOutputStream.writeByte(3);
        byte[] uid = UUID.randomUUID().toString().getBytes();
        dataOutputStream.writeInt(uid.length);
        dataOutputStream.write(uid);
        ByteBuffer buff = ByteBuffer.wrap(barray.toByteArray());
        channel.write(buff);

        ByteBuffer dst = BufferUtils.readFixedData(channel,5, ByteOrder.BIG_ENDIAN);
        if(dst.get() == 3){
            int position = dst.getInt();
            System.out.println(position);
        }
    }

    @Override
    public void onNotify(RowEntity entity, RowEntity entity2) {

    }
}
