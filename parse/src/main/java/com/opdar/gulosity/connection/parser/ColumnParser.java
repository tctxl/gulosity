package com.opdar.gulosity.connection.parser;

import com.opdar.gulosity.connection.entity.Column;
import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/21.
 */
public class ColumnParser implements Parser<Column> {
    public Column parser(ByteBuffer buffer) {
        Column column= new Column();
        column.setCatalog(new String(readColumn(buffer)));
        column.setSchema(new String(readColumn(buffer)));
        column.setTable(new String(readColumn(buffer)));
        column.setOrgTable(new String(readColumn(buffer)));
        column.setName(new String(readColumn(buffer)));
        column.setOrgName(new String(readColumn(buffer)));
        column.setCharacterSet(buffer.getShort());
        column.setLength(buffer.getInt());
        column.setType(buffer.get());
        column.setFlags(buffer.getShort());
        column.setDecimals(buffer.get());
        column.setFiller(buffer.getShort());

        if (buffer.hasRemaining()) {
            column.setDefinition(new String(readColumn(buffer)));
        }
        return column;
    }

    public byte[] readColumn(ByteBuffer buffer){
        int length = buffer.get();
        byte[] result = new byte[length];
        buffer.get(result);
        return result;
    }
}
