package com.opdar.gulosity.event.binlog;

import com.opdar.gulosity.base.Constants;
import com.opdar.gulosity.connection.entity.Column;
import com.opdar.gulosity.event.base.ChannelEvent;
import com.opdar.gulosity.utils.BufferUtils;
import com.opdar.gulosity.utils.MysqlUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Shey on 2016/8/25.
 */
public class TableMapEvent extends ChannelEvent {
    private long tableId;
    private short flag;
    //payload
    private int schemaNameLength;
    private String schemaName;
    private int tableNameLength;
    private String tableName;
    private int columnCount;
    private Column[] columns;
    private BitSet bitmap;// [len=(column_count + 8) / 7]
    private LinkedList<String> columnInfo;

    public TableMapEvent(BinlogHeader header, SocketChannel channel) {
        super(header, channel);
    }

    public void setColumnInfo(LinkedList<String> columnInfo) {
        this.columnInfo = columnInfo;
    }

    public LinkedList<String> getColumnInfo() {
        return columnInfo;
    }

    public class Column {
        private int columnTypeDef;
        private int columnMetaDef;

        public int getColumnTypeDef() {
            return columnTypeDef;
        }

        public int getColumnMetaDef() {
            return columnMetaDef;
        }
    }

    public void doing() {
        ByteBuffer buffer = BufferUtils.readFixedData(getChannel(), (int) (getHeader().getEventLength() - Constants.MYSQL.FIXED_EVENT_LENGTH));
        int postHeaderLen = getFormat().getPostHeader()[getHeader().getTypeCode() - 1];
        if (postHeaderLen == 6) {
            tableId = buffer.getInt();
        } else {
            tableId = BufferUtils.readLong(buffer, 6);
        }
        flag = buffer.getShort();
        schemaNameLength = buffer.get();
        schemaName = BufferUtils.readFixedString(buffer, schemaNameLength);
        buffer.get();
        tableNameLength = buffer.get();
        tableName = BufferUtils.readFixedString(buffer, tableNameLength);
        buffer.get();
        columnCount = buffer.get();
        columns = new Column[columnCount];
        byte[] types = new byte[columnCount];
        buffer.get(types);
        buffer.get();
        for (int i = 0; i < columnCount; i++) {
            int type = types[i];
            Column column = columns[i] = new Column();
            column.columnTypeDef = type & 0xff;
            switch (column.columnTypeDef) {
                case BLOB:
                case MEDIUM_BLOB:
                case LONG_BLOB:
                case DOUBLE:
                case FLOAT:
                case GEOMETRY:
                case TIME2:
                case DATETIME2:
                case TIMESTAMP2:
                    column.columnMetaDef = buffer.get();
                    break;
                case DECIMAL:
                case NEWDECIMAL:
                case VAR_STRING:
                case STRING:
                    int x = (buffer.get() << 8);
                    x += buffer.get();
                    column.columnMetaDef = x;
                    break;
                case VARCHAR:
                case BIT:
                    column.columnMetaDef = buffer.getShort();
                    break;
                default:
                    column.columnMetaDef = 0;
                    break;
            }
        }
        bitmap = BufferUtils.readBitmap(columnCount,buffer);

    }

    public long getTableId() {
        return tableId;
    }

    public short getFlag() {
        return flag;
    }

    public int getSchemaNameLength() {
        return schemaNameLength;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public int getTableNameLength() {
        return tableNameLength;
    }

    public String getTableName() {
        return tableName;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Column[] getColumns() {
        return columns;
    }

    public BitSet getBitmap() {
        return bitmap;
    }
}
