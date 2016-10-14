package com.opdar.gulosity.connection;

import com.opdar.gulosity.base.Constants;
import com.opdar.gulosity.connection.entity.Column;
import com.opdar.gulosity.connection.parser.Body;
import com.opdar.gulosity.connection.parser.ColumnParser;
import com.opdar.gulosity.connection.parser.RowParser;
import com.opdar.gulosity.connection.protocol.ErrorProtocol;
import com.opdar.gulosity.connection.protocol.HeaderProtocol;
import com.opdar.gulosity.entity.MysqlAuthInfoEntity;
import com.opdar.gulosity.utils.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Shey on 2016/8/19.
 */
public class MysqlConnection {
    private MysqlAuthInfoEntity authInfo;
    private SocketChannel channel;
    private int soTimeout = 30 * 1000;
    private int receiveBufferSize = 16 * 1024;
    private int sendBufferSize = 16 * 1024;
    private long connectionId = -1;
    private int charsetNumber = 33;//utf-8
    private byte[] scrumble;
    private String serverVersion = "";
    private AtomicBoolean connected = new AtomicBoolean();
    private Logger logger = LoggerFactory.getLogger(getClass());

    public AtomicBoolean getConnected() {
        return connected;
    }

    public MysqlConnection(MysqlAuthInfoEntity authInfo) {
        this.authInfo = authInfo;
    }

    public MysqlAuthInfoEntity getAuthInfo() {
        return authInfo;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void connect() throws IOException {
        this.channel = SocketChannel.open();
        channel.socket().setKeepAlive(true);
        channel.socket().setReuseAddress(true);
        channel.socket().setSoTimeout(soTimeout);
        channel.socket().setTcpNoDelay(true);
        channel.socket().setSendBufferSize(sendBufferSize);
        channel.connect(authInfo.getAddress());
        Body body = Body.get(channel);
        if (body.getState() < 0) {
            if (body.getState() == Constants.MYSQL.ERR_PACKET) {
                ErrorProtocol error = new ErrorProtocol();
                error.fromBytes(body.getBody());
                throw new IOException("handshake exception:\n" + error.toString());
            } else if (body.getState() == Constants.MYSQL.EOF) {
                throw new IOException("Unexpected EOF packet at handshake phase.");
            } else {
                throw new IOException("unpexpected packet with field_count=" + body.getState());
            }
        }
        initHandshakeV10(body.getBody());
        auth411(body.getHeader());
        body = Body.get(channel);
        if (body.getState() < 0) {
            if (body.getState() == -1) {
                ErrorProtocol err = new ErrorProtocol();
                err.fromBytes(body.getBody());
                throw new IOException("Error When doing Client Authentication:" + err.toString());
            } else if (body.getState() == -2) {
                throw new IOException("Not support old password.");
            } else {
                throw new IOException("unpexpected packet with field_count=" + body.getState());
            }
        } else {
            if (connected.compareAndSet(false, true)) {
                logger.info("Auth Success.");
            }
        }
    }

    //CLIENT_PROTOCOL_41
    private void auth411(HeaderProtocol header) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int clientFlag = 1 | 4 | 512 | 8192 | 32768;
        BufferUtils.writeInt(clientFlag, out);
        int maxPackageLength = 1 << 24;
        BufferUtils.writeInt(maxPackageLength, out);
        out.write(this.charsetNumber);
        //填充00
        out.write(new byte[23]);

        out.write(authInfo.getUserName().getBytes());
        out.write(0x00);
        if (authInfo.getPassWord() == null || authInfo.getPassWord().equals("")) {
            out.write(0x00);
        } else {
            //密码生成
            try {
                byte[] encryptedPassword = scramble411(authInfo.getPassWord().getBytes(), scrumble);
                BufferUtils.writeLength(encryptedPassword, out);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("加密失败", e);
            }
        }

        //初始化数据库
        if (authInfo.getDatabaseName() != null) {
            out.write(authInfo.getDatabaseName().getBytes());
            out.write(0x00);
        }
        byte[] auth = out.toByteArray();
        HeaderProtocol h = new HeaderProtocol();
        h.setBodyLength(auth.length);
        h.setSequence((byte) (header.getSequence() + 1));
        channel.write(new ByteBuffer[]{ByteBuffer.wrap(h.toBytes()), ByteBuffer.wrap(auth)});
    }

    //SHA1( password ) XOR SHA1( "20-bytes random data from server" <concat> SHA1( SHA1( password ) ) )
    public byte[] scramble411(byte[] pass, byte[] scrumble) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        //2次加密
        byte[] pass1 = md.digest(pass);
        md.reset();
        byte[] pass2 = md.digest(pass1);
        md.reset();
        //加混淆盐
        md.update(scrumble);
        //生成新密码
        byte[] pass3 = md.digest(pass2);
        for (int i = 0; i < pass3.length; i++) {
            //XOR
            pass3[i] = (byte) (pass3[i] ^ pass1[i]);
        }
        return pass3;
    }

    private void initHandshakeV10(ByteBuffer body) {
        //协议版本[1]
        int protocolVersion = body.get();
        //服务器版本[NUL]
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (body.hasRemaining()) {
            byte item = body.get();
            if (item == 0x00) {
                break;
            }
            out.write(item);
        }
        byte[] serverVersionBytes = out.toByteArray();
        serverVersion = new String(serverVersionBytes);
        // connection id[4]
        connectionId = (long) (body.get() & 0xFF) | (long) ((body.get() & 0xFF) << 8)
                | (long) ((body.get() & 0xFF) << 16) | (long) ((body.get() & 0xFF) << 24);
        //混淆串前部分 auth-plugin-data-part-1[8]
        byte[] apdp1 = new byte[8];
        body.get(apdp1);
        //filter[1]
        body.get();
        //capabilities[2]
        int capabilitieflags = (body.get() & 0xFF) | ((body.get() & 0xFF) << 8);
        // 获取charset[1]
        int charsetNumber = body.get();
        // 获取服务器状态status flags[2]
        int statusFlags = (body.get() & 0xFF) | ((body.get() & 0xFF) << 8);
        byte[] capabilitieflagsHigher = new byte[2];
        body.get(capabilitieflagsHigher);
        // 保留位 全部以00填充
        body.get(new byte[10]);
        int authDataLength = body.get();
        // 混淆穿后部分[12] 13字节最后位为00 意为结束字符串
        byte[] apdp2 = new byte[12];
        body.get(apdp2);
        //合并混淆串，用来作认证
        scrumble = new byte[apdp1.length + apdp2.length];
        System.arraycopy(apdp1, 0, scrumble, 0, apdp1.length);
        System.arraycopy(apdp2, 0, scrumble, apdp1.length, apdp2.length);

    }
    public void close() {
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            connected.set(false);
        }
    }

    public void waitConnect() {
        while (!connected.get()) {
            //wait
        }
    }
}
