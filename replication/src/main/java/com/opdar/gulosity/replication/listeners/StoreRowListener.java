package com.opdar.gulosity.replication.listeners;

import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.replication.base.Registry;
import com.opdar.gulosity.replication.base.StoreCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 协议
 * event 1 byte
 * schama.tablename length
 * columns info
 * columns data
 * Created by 俊帆 on 2016/10/27.
 */
public class StoreRowListener implements RowCallback {

    private Logger logger = LoggerFactory.getLogger(StoreRowListener.class);
    private Queue<StoreCallback> storeCallbacks = new ConcurrentLinkedQueue<StoreCallback>();

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
            fileOutputStream = new FileOutputStream(Registry.FILE_PATH, true);
            byte[] arrays = arrayOut.toByteArray();
            int len = arrays.length;
            fileOutputStream.write((byte) (len >>> 24));
            fileOutputStream.write((byte) (len >>> 16));
            fileOutputStream.write((byte) (len >>> 8));
            fileOutputStream.write((byte) (len & 0xFF));
            fileOutputStream.write(arrays);
            fileOutputStream.flush();
            for(StoreCallback storeCallback:storeCallbacks){
                int nextPosition = (int) fileOutputStream.getChannel().size();
                storeCallback.store(nextPosition - 4 - len,nextPosition);
            }
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

    public void addStoreCallback(StoreCallback storeCallback){
        storeCallbacks.add(storeCallback);
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
