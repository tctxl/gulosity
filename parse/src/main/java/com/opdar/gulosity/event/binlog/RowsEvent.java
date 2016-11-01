package com.opdar.gulosity.event.binlog;

import com.opdar.gulosity.base.Constants;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.event.base.ChannelEvent;
import com.opdar.gulosity.event.base.EventQueue;
import com.opdar.gulosity.event.other.SendCallbackEvent;
import com.opdar.gulosity.serializable.JavaSerializable;
import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.BitSet;

/**
 * Created by Shey on 2016/8/26.
 */
public class RowsEvent extends ChannelEvent {

    public enum Type{
        WRITEV0,WRITEV1,WRITEV2,UPDATEV0,UPDATEV1,UPDATEV2,DELETEV0,DELETEV1,DELETEV2
    }

    private long tableId;
    private int flags;
    private Type type;
    private int extraDataLength;
    private String extraData;

    private int columnsNumber;

    private BitSet columnsBefore;
    private BitSet columnsAfter;

    public RowsEvent(BinlogHeader header, SocketChannel channel, Type type) {
        super(header, channel);
        this.type = type;
    }

    public void doing() {
        //header
        ByteBuffer buffer = BufferUtils.readFixedData(getChannel(), (int) (getHeader().getEventLength() - Constants.MYSQL.FIXED_EVENT_LENGTH));
        int postHeaderLen = getFormat().getPostHeader()[getHeader().getTypeCode() - 1];
        if (postHeaderLen == 6) {
            tableId = buffer.getInt();
        } else {
            tableId = BufferUtils.readLong(buffer, 6);
        }
        flags = buffer.getShort();
        if(type == Type.WRITEV2 || type == Type.UPDATEV2 || type == Type.DELETEV2){
            extraDataLength = buffer.getShort();
            extraData = BufferUtils.readFixedString(buffer,extraDataLength-2);
            buffer.get();
        }
        //body
        columnsNumber = buffer.get();
        columnsBefore = BufferUtils.readBitmap(columnsNumber, buffer);
        columnsAfter = columnsBefore;
        if (type == Type.UPDATEV1 || type == Type.UPDATEV2){
            columnsAfter = BufferUtils.readBitmap(columnsNumber, buffer);
        }
        //row buff
        TableMapEvent table = getTable(tableId);
        TableMapEvent.Column[] columns = table.getColumns();

        RowEntity columnValues = columnValueGet(columnsBefore, buffer, columns);
        RowEntity updateAfter = null;
        if (type == Type.UPDATEV1 || type == Type.UPDATEV2){
            updateAfter = columnValueGet(columnsAfter,buffer, columns);
        }
        EventQueue.getInstance().addEvent(SendCallbackEvent.create(columnValues,updateAfter));
    }

    private RowEntity columnValueGet(BitSet columnsBit, ByteBuffer buffer, TableMapEvent.Column[] columns) {
        TableMapEvent tableMap = getTable(tableId);

        RowEntity rowEntity = new RowEntity(columns.length,type,tableMap.getSchemaName(),tableMap.getTableName(),tableMap.getColumnInfo(),tableId);
        int nullBitLength = 0;
        for(int i=0;i<columnsNumber;i++){
            if(columnsBit.get(i))
                nullBitLength++;
        }
        BitSet nullBits = BufferUtils.readBitmap(nullBitLength, buffer);
        for(int i=0;i<columns.length;i++){
            if (!columnsBit.get(i)) {
                continue;
            }

            TableMapEvent.Column column = columns[i];
            if(!nullBits.get(i)){
                //is not null
                try{
                    JavaSerializable serializable = JavaSerializable.get(column.getColumnTypeDef());
                    Object value = serializable.getValue(column.getColumnMetaDef(),buffer);
                    rowEntity.set(i,column.getColumnTypeDef(),value);
                }catch (Exception e){
                    System.out.println("index : "+i+" , column type is "+ column.getColumnTypeDef());
                    System.out.println(column.getColumnTypeDef());
                    throw new RuntimeException(e);
                }
            }else{
                rowEntity.set(i,column.getColumnTypeDef(),null);
            }
        }
        return rowEntity;
    }

}
