package com.opdar.gulosity.replication.listeners;

import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.replication.base.PositionIndexManager;
import com.opdar.gulosity.replication.base.Registry;
import com.opdar.gulosity.replication.base.StoreCallback;
import com.opdar.gulosity.utils.BufferUtils;
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
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    private PositionIndexManager positionIndexManager = new PositionIndexManager();

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
            File file = new File(Registry.FILE_PATH);
            fileOutputStream = new FileOutputStream(file, true);
            byte[] arrays = arrayOut.toByteArray();
            long currentPosition = fileOutputStream.getChannel().size();
            if(currentPosition == 0){
                //write version
                writeVersion(fileOutputStream);
            }
            int len = arrays.length;
            if((currentPosition+len )/1024 > Registry.MAX_SIZE){
                compress(file,positionIndexManager.getVersion());
                fileOutputStream.close();
                file.delete();
                fileOutputStream = new FileOutputStream(file, true);
                //write version
                writeVersion(fileOutputStream);
                positionIndexManager.delete();
                positionIndexManager = new PositionIndexManager();
            }
            fileOutputStream.write((byte) (len >>> 24));
            fileOutputStream.write((byte) (len >>> 16));
            fileOutputStream.write((byte) (len >>> 8));
            fileOutputStream.write((byte) (len & 0xFF));
            fileOutputStream.write(arrays);
            fileOutputStream.flush();
            long nextPosition =  fileOutputStream.getChannel().size();
            positionIndexManager.addIndex(currentPosition,len+4);
            for(StoreCallback storeCallback:storeCallbacks){
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

    private int writeVersion(FileOutputStream fileOutputStream) throws IOException {
        int version = positionIndexManager.getVersion()+1;
        fileOutputStream.write((byte) (version >>> 24));
        fileOutputStream.write((byte) (version >>> 16));
        fileOutputStream.write((byte) (version >>> 8));
        fileOutputStream.write((byte) (version & 0xFF));
        return version;
    }

    private void compress(File file,int version)  {
        String zipFile = file+"."+version+".zip";
        BufferedInputStream bis = null;ZipOutputStream zout = null;
        try{
            CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(zipFile),
                    new CRC32());
            zout = new ZipOutputStream(cos);
            bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(file.getName());
            zout.putNextEntry(entry);
            int count;
            byte data[] = new byte[1024];
            while ((count = bis.read(data, 0, 1024)) != -1) {
                zout.write(data, 0, count);
            }
        }catch (Exception ignored){}finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (zout != null) {
                    zout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
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
