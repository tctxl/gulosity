package com.opdar.gulosity.replication.listeners;

import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.event.binlog.RowsEvent.Type;
import com.opdar.gulosity.replication.deserializer.JavaDeserializer;
import com.opdar.gulosity.utils.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.opdar.gulosity.event.binlog.RowsEvent.Type.WRITEV2;

/**
 * 协议
 * event 1 byte
 * schama.tablename length
 * columns info
 * columns data
 * Created by 俊帆 on 2016/10/27.
 */
public class StoreRowListener implements RowCallback {

    //store file path
    private String storeFilePath = "file.dat";
    //unit is kb
    private int maxSize = 1024;
    private Logger logger = LoggerFactory.getLogger(StoreRowListener.class);

    @Override
    public void onNotify(RowEntity entity, RowEntity entity2) {
        ByteArrayOutputStream arrayOut = null;
        DataOutputStream out = new DataOutputStream(arrayOut = new ByteArrayOutputStream());
        int event = 0;
        switch (entity.getEventType()) {
            case WRITEV1:
            case WRITEV2:
                event = 1;
                break;
            case DELETEV1:
            case DELETEV2:
                event = 2;
                break;
            case UPDATEV1:
            case UPDATEV2:
                event = 3;
                break;
        }
        try {
            out.writeByte(event);
            out.writeLong(entity.getTableId());
            write(out, entity.getTableName());

            writeList(out, entity.getColumnInfo());
            writeIntArray(out, entity.getResultType());
            write(out, entity.getAll());

            if (event == 3) {
                writeList(out, entity2.getColumnInfo());
                writeIntArray(out, entity.getResultType());
                write(out, entity2.getAll());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(storeFilePath, true);
            byte[] arrays = arrayOut.toByteArray();
            int len = arrays.length;

            fileOutputStream.write((byte) (len >>> 24));
            fileOutputStream.write((byte) (len >>> 16));
            fileOutputStream.write((byte) (len >>> 8));
            fileOutputStream.write((byte) (len & 0xFF));
            fileOutputStream.write(arrays);
            fileOutputStream.flush();
            logger.info("store file size : {}", fileOutputStream.getChannel().size());
            long ia = fileOutputStream.getChannel().size() - arrays.length - 4;
            System.out.println(ia);
            read((int) ia);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void read(int seek) {

        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(storeFilePath, "r");
            randomAccessFile.seek(seek);
            int length = randomAccessFile.readInt();
            byte[] b = new byte[length];
            randomAccessFile.read(b);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            int event = buffer.get();
            long tableId = buffer.getLong();
            length = buffer.getInt();
            String[] tableName = BufferUtils.readFixedString(buffer, length).split("\\.", 2);
            RowEntity rowEntity = getRowEntity(buffer, event, tableId, tableName);
            logger.info("read length : {}", length);
            logger.info("row1 : {}", rowEntity);
            if (event == 3) {
                RowEntity rowEntity2 = getRowEntity(buffer, event, tableId, tableName);
                logger.info("row2 : {}", rowEntity2);
            }

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

    private RowEntity getRowEntity(ByteBuffer buffer, int event, long tableId, String[] tableName) {
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
        Type type = event == 1 ? WRITEV2 : event == 2 ? Type.DELETEV2 : Type.UPDATEV2;
        RowEntity rowEntity = new RowEntity(columnSize, type, tableName[0], tableName[1], columnInfo, tableId);
        for (int i = 0; i < columnSize; i++) {
            int len = buffer.getInt();
            if (len != 0) {
                byte[] result = BufferUtils.readFixedData(buffer, len);
                int rt = resultType[i];
                Object o = null;
                try {
                    o = JavaDeserializer.get(rt).getValue(result);
                } catch (Exception ignored) {}
                rowEntity.set(i, rt, o);
            }
        }
        return rowEntity;
    }


    private void write(DataOutputStream out, Object[] list) throws IOException {
        this.writeList(out, Arrays.asList(list));
    }

    private void writeList(DataOutputStream out, List<? extends Object> list) throws IOException {
        out.writeByte(list.size());
        for (Object s : list) {
            if (s != null) {
                byte[] bytes = null;
                if (s instanceof byte[]) {
                    bytes = (byte[]) s;
                } else if (s instanceof ByteBuffer) {
                    bytes = ((ByteBuffer) s).array();
                } else if (s instanceof Date) {
                    bytes = new Timestamp(((Date) s).getTime()).toString().getBytes();
                } else {
                    bytes = s.toString().getBytes();
                }
                out.writeInt(bytes.length);
                out.write(bytes);
            } else {
                out.writeInt(0);
            }
        }
    }

    private void writeIntArray(DataOutputStream out, int[] list) throws IOException {
        out.writeByte(list.length);
        for (int i : list) {
            out.writeInt(i);
        }
    }

    private void write(DataOutputStream out, Object obj) throws IOException {
        if (obj == null)
            out.writeInt(0);
        else {
            byte[] result = obj.toString().getBytes();
            out.writeInt(result.length);
            out.write(result);
        }
    }
}
