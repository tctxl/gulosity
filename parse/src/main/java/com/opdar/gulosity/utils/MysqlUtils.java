package com.opdar.gulosity.utils;

import com.opdar.gulosity.base.Constants;
import com.opdar.gulosity.connection.entity.Column;
import com.opdar.gulosity.connection.parser.Body;
import com.opdar.gulosity.connection.parser.ColumnParser;
import com.opdar.gulosity.connection.parser.RowParser;
import com.opdar.gulosity.connection.protocol.HeaderProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by 俊帆 on 2016/10/14.
 */
public class MysqlUtils {

    private static Logger logger = LoggerFactory.getLogger(MysqlUtils.class);

    public static synchronized List<Map<Column, String>> query(SocketChannel channel,String CMD) {
        List<Map<Column, String>> list = new LinkedList<Map<Column, String>>();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(0x03);
        try {
            outputStream.write(CMD.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            byte[] outArray = outputStream.toByteArray();
            HeaderProtocol headerProtocol = new HeaderProtocol();
            headerProtocol.setBodyLength(outArray.length);
            headerProtocol.setSequence((byte) 0);
            channel.write(new ByteBuffer[]{ByteBuffer.wrap(headerProtocol.toBytes()), ByteBuffer.wrap(outArray)});
            Body body = Body.get(channel);
            body.check();
            ByteBuffer bodyBuf = body.getBody();
            if (body.getHeader().getSequence() == 1) {
                //获取列(Column)数目
                int columnCount = bodyBuf.get();
                List<Column> columns = new LinkedList<Column>();
                for (int i = 0; i < columnCount; i++) {
                    body = Body.get(channel);
                    ColumnParser columnParser = new ColumnParser();
                    Column column = columnParser.parser(body.getBody());
                    columns.add(column);
                }
                //列循环完毕,读取EOF
                body = Body.get(channel);
                if (body.getBody().get() != Constants.MYSQL.EOF) {
                    logger.error("EOF读取错误.");
                }
                //获取行数据
                while (true) {
                    body = Body.get(channel);
                    if (body.getState() == Constants.MYSQL.EOF) {
                        break;
                    }
                    RowParser parser = new RowParser();
                    List<String> row = parser.parser(body.getBody());
                    Map<Column, String> map = new HashMap<Column, String>();
                    for (int i = 0; i < columns.size(); i++) {
                        Column column = columns.get(i);
                        map.put(column, row.get(i));
                    }
                    list.add(map);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
