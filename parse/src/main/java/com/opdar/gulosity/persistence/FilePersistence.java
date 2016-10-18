package com.opdar.gulosity.persistence;

import com.opdar.gulosity.entity.Position;

import java.io.*;

/**
 * Created by 俊帆 on 2016/10/17.
 */
public class FilePersistence implements IPersistence {
    private Position position = new Position();
    private File file;

    public FilePersistence(String s) {
        DataInputStream ois = null;
        try {
            file = new File(s);
            if (file.exists()) {
                ois = new DataInputStream(new FileInputStream(file));
                position.setNextPosition(ois.readLong());
                int fileNameLength = ois.readInt();
                byte[] b = new byte[fileNameLength];
                ois.read(b);
                position.setFileName(new String(b));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isInit(){
        if(position.getNextPosition() != 0 && position.getFileName() != null){
            return true;
        }
        return false;
    }

    @Override
    public void commit(long position) {
        if (position > 0) {
            this.position.setNextPosition(position);
            store();
        }
    }

    @Override
    public long getPosition() {
        if (position != null)
            return position.getNextPosition();
        return 0;
    }

    @Override
    public String getFileName() {
        return position.getFileName();
    }

    @Override
    public void setFileName(String fileName) {
        position.setFileName(fileName);
        store();
    }

    private void store() {
        DataOutputStream oos = null;
        try {
            oos = new DataOutputStream(new FileOutputStream(file));
            oos.writeLong(position.getNextPosition());
            byte[] filename = position.getFileName().getBytes();
            oos.writeInt(filename.length);
            oos.write(filename);
            oos.flush();
        } catch (Exception ignored) {
        } finally {
            if (oos != null) try {
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
