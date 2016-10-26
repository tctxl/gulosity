package com.opdar.gulosity.spring.configs;

import com.opdar.gulosity.base.ConnectionListener;
import com.opdar.gulosity.entity.MysqlAuthInfoEntity;
import com.opdar.gulosity.error.ConnectionCloseException;
import com.opdar.gulosity.error.NotSupportBinlogException;
import com.opdar.gulosity.event.base.Event;
import com.opdar.gulosity.event.base.EventQueue;
import com.opdar.gulosity.event.base.ResultEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class JdbcConfiguration implements ConnectionListener {
    private String host;
    private Integer port;
    private String userName;
    private String passWord;
    private String defaultDatabaseName;

    private long serverId;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private boolean runInMainThread = false;
    private MysqlAuthInfoEntity authInfo;

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
        authInfo = new MysqlAuthInfoEntity(new InetSocketAddress(host, port), userName, passWord, serverId);
        authInfo.setDatabaseName(defaultDatabaseName);
        EventQueue.getInstance().setHandler(new EventQueue.Handler() {
            @Override
            public void error(Event event, Exception e) {
                super.error(event, e);
                if (e instanceof NotSupportBinlogException) {
                    e.printStackTrace();
                    EventQueue.getInstance().stop();
                } else if (e instanceof ConnectionCloseException) {
                    JdbcConfiguration.this.onClose();
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
        EventQueue.getInstance().addEvent(new ConnectionEvent(authInfo));
    }


    public void setRunInMainThread(boolean runInMainThread) {
        this.runInMainThread = runInMainThread;
    }

    @Override
    public void onClose() {
        try {
            //waiting 3 second
            Thread.sleep(1000);
            //reconnected
            if(logger.isDebugEnabled()){
                logger.debug("connecting...");
            }
            EventQueue.getInstance().addEvent(new ConnectionEvent(authInfo));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
