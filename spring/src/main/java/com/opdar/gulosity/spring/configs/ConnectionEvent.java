package com.opdar.gulosity.spring.configs;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.base.TableCache;
import com.opdar.gulosity.base.TableCacheCallback;
import com.opdar.gulosity.connection.MysqlConnection;
import com.opdar.gulosity.entity.MysqlAuthInfoEntity;
import com.opdar.gulosity.error.ConnectionCloseException;
import com.opdar.gulosity.event.base.Event;
import com.opdar.gulosity.event.base.EventQueue;
import com.opdar.gulosity.event.other.SalveQueryEvent;
import sun.misc.Unsafe;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by 俊帆 on 2016/10/26.
 */
public class ConnectionEvent implements Event {
    private MysqlAuthInfoEntity authInfo;
    private MysqlConnection connection;
    public ConnectionEvent(MysqlAuthInfoEntity authInfo) {
        this.authInfo = authInfo;
    }

    @Override
    public void doing() {
        try {
            caching(authInfo);
            connection = new MysqlConnection(authInfo);
            connection.connect();
            connection.waitConnect();
            EventQueue.getInstance().addEvent(new SalveQueryEvent(connection));
        } catch (Exception e) {
            throw new ConnectionCloseException(e);
        }
    }

    public void caching(MysqlAuthInfoEntity authInfo) {
        TableCache tableCache = new TableCache(authInfo, new TableCacheCallback() {
            @Override
            public void callback(Map<String, LinkedList<String>> tables) {
                MysqlContext.putTableCache(tables);
            }
        });
        tableCache.run();
    }

}
