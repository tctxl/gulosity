package com.opdar.gulosity.spring.configs;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.base.TableCache;
import com.opdar.gulosity.base.TableCacheCallback;
import com.opdar.gulosity.connection.MysqlConnection;
import com.opdar.gulosity.entity.MysqlAuthInfoEntity;
import com.opdar.gulosity.error.NotSupportBinlogException;
import com.opdar.gulosity.event.base.Event;
import com.opdar.gulosity.event.base.EventQueue;
import com.opdar.gulosity.event.base.ResultEvent;
import com.opdar.gulosity.event.other.SalveQueryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class JdbcConfiguration {
    private String host;
    private Integer port;
    private String userName;
    private String passWord;
    private String defaultDatabaseName;

    private long serverId;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private MysqlConnection connection;
    private boolean runInMainThread = false;

    public JdbcConfiguration() {
    }

    public JdbcConfiguration(String host, Integer port, String userName, String passWord, String defaultDatabaseName, long serverId) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.passWord = passWord;
        this.defaultDatabaseName = defaultDatabaseName;
        this.serverId = serverId;
    }

    public void init() {
        MysqlAuthInfoEntity authInfo = new MysqlAuthInfoEntity(new InetSocketAddress(host, port), userName, passWord, serverId);
        authInfo.setDatabaseName(defaultDatabaseName);
        caching(authInfo);
        connection = new MysqlConnection(authInfo);
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
        } catch (IOException e) {
            e.printStackTrace();
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

    public MysqlConnection getConnection() {
        return connection;
    }

    public void setRunInMainThread(boolean runInMainThread) {
        this.runInMainThread = runInMainThread;
    }
}
