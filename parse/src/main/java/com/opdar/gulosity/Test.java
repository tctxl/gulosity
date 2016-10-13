package com.opdar.gulosity;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.connection.MysqlConnection;
import com.opdar.gulosity.entity.MysqlAuthInfoEntity;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.error.NotSupportBinlogException;
import com.opdar.gulosity.event.base.Event;
import com.opdar.gulosity.event.base.EventQueue;
import com.opdar.gulosity.event.base.ResultEvent;
import com.opdar.gulosity.event.other.SalveQueryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by Shey on 2016/8/19.
 */
public class Test {

    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) {
        MysqlAuthInfoEntity authInfo = new MysqlAuthInfoEntity(new InetSocketAddress("192.168.1.147", 10912), "root", "123456");
        authInfo.setDatabaseName("mysql");
        MysqlConnection connection = new MysqlConnection(authInfo);
        try {
            connection.connect();
            connection.waitConnect();
            EventQueue.getInstance().setHandler(new EventQueue.Handler() {
                @Override
                public void error(Event event, Exception e) {
                    super.error(event, e);
                    if (event instanceof SalveQueryEvent) {
                        e.printStackTrace();
                        if (e instanceof NotSupportBinlogException) {
                            EventQueue.getInstance().stop();
                        }
                    }
                }

                @Override
                public void success(Event event) {
                    super.success(event);
                    if (event instanceof ResultEvent) {
                        Object result = ((ResultEvent) event).getResult();
                        logger.info(result.toString());
                    }
                }
            });
            EventQueue.getInstance().addEvent(new SalveQueryEvent(connection));
            MysqlContext.addRowCallback(new RowCallback() {
                public void onNotify(RowEntity entity, RowEntity entity2) {
                    switch (entity.getEventType()) {
                        case WRITEV1:
                        case WRITEV2:
                            logger.debug("新增数据：" + entity);
                            break;
                        case DELETEV1:
                        case DELETEV2:
                            logger.debug("删除数据：" + entity);
                            break;
                        case UPDATEV1:
                        case UPDATEV2:
                            logger.debug("更新前：" + entity);
                            logger.debug("更新后：" + entity2);
                            break;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
