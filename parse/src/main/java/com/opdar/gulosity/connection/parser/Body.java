package com.opdar.gulosity.connection.parser;

import com.opdar.gulosity.connection.protocol.ErrorProtocol;
import com.opdar.gulosity.connection.protocol.HeaderProtocol;
import com.opdar.gulosity.utils.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

/**
 * Created by Shey on 2016/8/21.
 */
public class Body {

    private static final Logger logger = LoggerFactory.getLogger(Body.class);
    private HeaderProtocol header = new HeaderProtocol();
    private ByteBuffer body;
    private byte state;

    public HeaderProtocol getHeader() {
        return header;
    }

    public void setHeader(HeaderProtocol header) {
        this.header = header;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public ByteBuffer getBody() {
        return body;
    }

    public void setBody(ByteBuffer body) {
        this.body = body;
    }

    public static Body get(SocketChannel channel){
        try {
            Body body = new Body();
            HeaderProtocol header = body.header;
            //链接并获取MYSQL的头信息
            header.fromBytes(readBuffer(channel, 4).array());
            int length = header.getBodyLength();
            //解析并获取到body长度
            body.body = readBuffer(channel, length);
            body.body.order(ByteOrder.LITTLE_ENDIAN);
            body.body.mark();
            byte state = body.body.get();
            body.state = state;
            body.body.reset();
            return body;
        } catch (IOException e) {
            logger.error("parse failure.");
            throw new RuntimeException(e);
        }
    }

    public static void send(SocketChannel channel,ByteBuffer array,int sequence) throws IOException {
        HeaderProtocol header = new HeaderProtocol();
        header.setBodyLength(array.limit());
        header.setSequence((byte) sequence);
        channel.write(new ByteBuffer[]{ByteBuffer.wrap(header.toBytes()),
                array});
    }

    public static void send(SocketChannel channel,byte[] array,int sequence) throws IOException {
        HeaderProtocol header = new HeaderProtocol();
        header.setBodyLength(array.length);
        header.setSequence((byte) sequence);
        channel.write(new ByteBuffer[]{ByteBuffer.wrap(header.toBytes()),
                ByteBuffer.wrap(array)});
    }

    public static ByteBuffer readBuffer(SocketChannel ch, int len) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(len);
        while (buffer.hasRemaining()) {
            int read = ch.read(buffer);
            if (read == -1) {
                throw new IOException("Unexpected End Stream");
            }
        }
        buffer.position(0);
        return buffer;
    }

    public void check() {
        body.mark();
        int checkValue = body.get();
        body.reset();
        if(checkValue < 0){
            ErrorProtocol errorProtocol = new ErrorProtocol();
            errorProtocol.fromBytes(body);
            logger.error(errorProtocol.toString());
        }
    }
}
