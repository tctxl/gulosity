package com.opdar.gulosity.replication.base;

import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.event.binlog.RowsEvent;
import com.opdar.gulosity.replication.deserializer.JavaDeserializer;
import com.opdar.gulosity.utils.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import static com.opdar.gulosity.event.binlog.RowsEvent.Type.WRITEV2;

/**
 * Created by 俊帆 on 2016/11/1.
 */
public class StoreFileUtils {
    private final static Logger logger = LoggerFactory.getLogger(StoreFileUtils.class);

    public static void read(String path, int seek) {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(path, "r");
            randomAccessFile.seek(seek);
            int length = randomAccessFile.readInt();
            byte[] b = new byte[length];
            randomAccessFile.read(b);
            parseRow(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void parseRow(byte[] b) {
        ByteBuffer buffer = ByteBuffer.wrap(b);
        parseRow(buffer,null);
    }

    public static void parseRow(ByteBuffer buffer, RowCallback rowCallback) {
        int event = buffer.get();
        long tableId = buffer.getLong();
        int length = buffer.getInt();
        String[] tableName = BufferUtils.readFixedString(buffer, length).split("\\.", 2);
        RowEntity rowEntity = getRowEntity(buffer, event, tableId, tableName);
        RowEntity rowEntity2 = null;
        if (event == 3) {
            rowEntity2 = getRowEntity(buffer, event, tableId, tableName);
            logger.info("row2 : {}", rowEntity2);
        }
        if(rowCallback != null){
            rowCallback.onNotify(rowEntity,rowEntity2);
        }
    }

    private static RowEntity getRowEntity(ByteBuffer buffer, int event, long tableId, String[] tableName) {
        int columnSize = buffer.get();
        LinkedList<String> columnInfo = new LinkedList<String>();
        for (int i = 0; i < columnSize; i++) {
            int len = buffer.getInt();
            String result = BufferUtils.readFixedString(buffer, len);
            columnInfo.add(result);
        }
        columnSize = buffer.get();
        int[] resultType = new int[columnSize];
        for (int i = 0; i < columnSize; i++) {
            int rt = buffer.getInt();
            resultType[i] = rt;
        }
        columnSize = buffer.get();
        RowsEvent.Type type = event == 1 ? WRITEV2 : event == 2 ? RowsEvent.Type.DELETEV2 : RowsEvent.Type.UPDATEV2;
        RowEntity rowEntity = new RowEntity(columnSize, type, tableName[0], tableName[1], columnInfo, tableId);
        for (int i = 0; i < columnSize; i++) {
            int len = buffer.getInt();
            if (len != 0) {
                byte[] result = BufferUtils.readFixedData(buffer, len);
                int rt = resultType[i];
                Object o = null;
                try {
                    o = JavaDeserializer.get(rt).getValue(result);
                } catch (Exception ignored) {
                }
                rowEntity.set(i, rt, o);
            }
        }
        return rowEntity;
    }
}
