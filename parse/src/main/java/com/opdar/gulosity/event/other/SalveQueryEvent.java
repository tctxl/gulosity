package com.opdar.gulosity.event.other;

import com.opdar.gulosity.base.Constants;
import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.connection.MysqlConnection;
import com.opdar.gulosity.connection.entity.Column;
import com.opdar.gulosity.connection.entity.SalveEntity;
import com.opdar.gulosity.connection.protocol.HeaderProtocol;
import com.opdar.gulosity.error.NotSupportBinlogException;
import com.opdar.gulosity.event.base.Event;
import com.opdar.gulosity.event.binlog.SalveFetchEvent;
import com.opdar.gulosity.persistence.IPersistence;
import com.opdar.gulosity.utils.BufferUtils;
import com.opdar.gulosity.utils.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Shey on 2016/8/22.
 */
public class SalveQueryEvent implements Event {
    private final MysqlConnection connection;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public SalveQueryEvent(MysqlConnection connection) {
        this.connection = connection;
    }

    public void doing() {
        SalveEntity salveEntity  = new SalveEntity();
        List<Map<Column, String>> result = MysqlUtils.query(connection.getChannel(),Constants.SHOW_MASTER_STATUS);
        if(result.size() > 0){
            IPersistence persistence = MysqlContext.getPersistence();
            if(persistence != null && persistence.getFileName() != null){
                salveEntity.setPosition((int) persistence.getPosition());
                salveEntity.setFile(persistence.getFileName());
            }else{
                Map<Column, String> map = result.get(0);
                for(Iterator<Map.Entry<Column, String>> it = map.entrySet().iterator();it.hasNext();){
                    Map.Entry<Column, String> column = it.next();
                    if(column.getKey().getName().equals(Constants.POSITION)){
                        salveEntity.setPosition(Integer.valueOf(column.getValue()));
                    }
                    if(column.getKey().getName().equals(Constants.FILE)){
                        salveEntity.setFile(column.getValue());
                    }
                }
            }
            salveEntity.setSalveId((int) connection.getAuthInfo().getServerId());

            //salve协议准备发送(binlog dump)

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write((byte)  0x12);
            BufferUtils.writeInt(salveEntity.getPosition(), out);

            int binlog_flags = 0;
            out.write(binlog_flags);
            out.write(0x00);
            BufferUtils.writeInt(salveEntity.getSalveId(), out);
            if (salveEntity.checkBinlogFile()) {
                try {
                    out.write(salveEntity.getFile().getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            try {
                byte[] array = out.toByteArray();
                HeaderProtocol header = new HeaderProtocol();
                header.setBodyLength(array.length);
                header.setSequence((byte) 0x00);
                connection.getChannel().write(new ByteBuffer[]{ByteBuffer.wrap(header.toBytes()),
                        ByteBuffer.wrap(array)});
                new SalveFetchEvent(connection).doing();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            throw new NotSupportBinlogException("Master not support binlog.");
        }
    }
}
