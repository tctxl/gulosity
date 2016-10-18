package com.opdar.gulosity.event;

import com.opdar.gulosity.base.Constants;
import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.connection.protocol.HeaderProtocol;
import com.opdar.gulosity.event.base.ChannelEvent;
import com.opdar.gulosity.event.base.Event;
import com.opdar.gulosity.event.binlog.*;
import com.opdar.gulosity.persistence.IPersistence;
import com.opdar.gulosity.utils.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import static com.opdar.gulosity.base.Constants.Event.*;
/**
 * http://dev.mysql.com/doc/internals/en/binlog-event-type.html
 * Created by Shey on 2016/8/22.
 */
public class EventType {
    public static Event get(SocketChannel channel) throws IOException {
        return get(channel,null);
    }

    public static Event get(SocketChannel channel,ChannelEvent prev) throws IOException {
        HeaderProtocol headerProtocol = new HeaderProtocol();
        headerProtocol.fromBytes(BufferUtils.readFixedData(channel, 4).array());

        ByteBuffer eventBuffer = BufferUtils.readFixedData(channel, Constants.MYSQL.FIXED_EVENT_LENGTH + 1);
        eventBuffer.position(1);
        BinlogHeader header = new BinlogHeader(eventBuffer);
        ChannelEvent event = null;
        switch (header.getTypeCode()) {
            case ROTATE_EVENT: {
                event = new RotateEvent(header, channel);
                MysqlContext.add(event);
                break;
            }
            case FORMAT_DESCRIPTION_EVENT: {
                event = new FormatDescriptionEvent(header, channel);
                MysqlContext.add(event);
                break;
            }
            case QUERY_EVENT: {
                event = new QueryEvent(header, channel);
                MysqlContext.add(event);
                break;
            }
            case TABLE_MAP_EVENT: {
                event = new TableMapEvent(header, channel);
                break;
            }
            case WRITE_ROWS_EVENTv1: {
                event = new RowsEvent(header,channel, RowsEvent.Type.WRITEV1);
                break;
            }
            case WRITE_ROWS_EVENTv2: {
                event = new RowsEvent(header,channel, RowsEvent.Type.WRITEV2);
                break;
            }
            case UPDATE_ROWS_EVENTv1: {
                event = new RowsEvent(header,channel, RowsEvent.Type.UPDATEV1);
                break;
            }
            case UPDATE_ROWS_EVENTv2: {
                event = new RowsEvent(header,channel, RowsEvent.Type.UPDATEV2);
                break;
            }
            case DELETE_ROWS_EVENTv1: {
                event = new RowsEvent(header,channel, RowsEvent.Type.DELETEV1);
                break;
            }
            case DELETE_ROWS_EVENTv2: {
                event = new RowsEvent(header,channel, RowsEvent.Type.DELETEV2);
                break;
            }
            case XID_EVENT: {
                event = new XidEvent(header, channel);
                MysqlContext.add(event);
                break;
            }
        }
        if(event != null){
            if(prev != null){
                event.setPrev(prev);
            }else{
                event.setPrev(null);
            }
            event.doing();
            if(event instanceof TableMapEvent)
                MysqlContext.addTable((TableMapEvent) event);
        }
        if (event != null) {
            IPersistence persistence = MysqlContext.getPersistence();
            persistence.commit(event.getHeader().getNextPosition());
        }
        return event;
    }
}
