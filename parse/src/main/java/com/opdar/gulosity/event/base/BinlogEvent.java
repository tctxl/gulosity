package com.opdar.gulosity.event.base;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.event.binlog.BinlogHeader;
import com.opdar.gulosity.event.binlog.FormatDescriptionEvent;
import com.opdar.gulosity.event.binlog.RotateEvent;
import com.opdar.gulosity.event.binlog.TableMapEvent;

/**
 * Created by Shey on 2016/8/27.
 */
public abstract class BinlogEvent implements Event {

    public static final int DECIMAL = 0;
    public static final int TINY = 1;
    public static final int SHORT = 2;
    public static final int LONG = 3;
    public static final int FLOAT = 4;
    public static final int DOUBLE = 5;
    public static final int NULL = 6;
    public static final int TIMESTAMP = 7;
    public static final int LONGLONG = 8;
    public static final int INT24 = 9;
    public static final int DATE = 0x0a;
    public static final int TIME = 0x0b;
    public static final int DATETIME = 0x0c;
    public static final int YEAR = 0x0d;
    public static final int NEWDATE = 0x0e;
    public static final int VARCHAR = 0x0f;
    public static final int BIT = 0x10;
    public static final int TIMESTAMP2 = 0x11;
    public static final int DATETIME2 = 0x12;
    public static final int TIME2 = 0x13;
    public static final int NEWDECIMAL = 0xf6;
    public static final int ENUM = 0xf7;
    public static final int SET = 0xf8;
    public static final int TINY_BLOB = 0xf9;
    public static final int MEDIUM_BLOB = 0xfa;
    public static final int LONG_BLOB = 0xfb;
    public static final int BLOB = 0xfc;
    public static final int VAR_STRING = 0xfd;
    public static final int STRING = 0xfe;
    public static final int GEOMETRY = 0xff;

    protected BinlogHeader header;

    public BinlogEvent(BinlogHeader header) {
        this.header = header;
    }

    public TableMapEvent getTable(Long id) {
        return MysqlContext.getTable(id);
    }

    public void setPosition(long position) {
        MysqlContext.get(RotateEvent.class).setPosition(position);
    }

    public long getPosition() {
        return MysqlContext.get(RotateEvent.class).getPosition();
    }

    public void setBinlogName(String binlogName) {
        MysqlContext.get(RotateEvent.class).setFileName(binlogName);
    }

    public RotateEvent getBinlogName() {
        return MysqlContext.get(RotateEvent.class).getBinlogName();
    }

    public FormatDescriptionEvent getFormat() {
        return MysqlContext.get(FormatDescriptionEvent.class);
    }

    public BinlogHeader getHeader() {
        return header;
    }

    public void setHeader(BinlogHeader header) {
        this.header = header;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Long){
            return (Long)obj == header.getNextPosition();
        }
        if(obj instanceof BinlogEvent){
            return ((BinlogEvent) obj).header.getNextPosition() == header.getNextPosition();
        }
        return super.equals(obj);
    }
}
