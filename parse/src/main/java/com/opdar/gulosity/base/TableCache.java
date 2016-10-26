package com.opdar.gulosity.base;

import com.opdar.gulosity.connection.MysqlConnection;
import com.opdar.gulosity.connection.entity.Column;
import com.opdar.gulosity.entity.MysqlAuthInfoEntity;
import com.opdar.gulosity.utils.MysqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by 俊帆 on 2016/10/14.
 */
public class TableCache implements Runnable {
    public static final String INFORMATION_SCHEMA = "information_schema";
    public static final String MYSQL = "mysql";
    public static final String PERFORMANCE_SCHEMA = "performance_schema";
    private MysqlAuthInfoEntity authInfo;
    private TableCacheCallback cacheCallback;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public TableCache(MysqlAuthInfoEntity authInfo,TableCacheCallback cacheCallback) {
        this.authInfo = authInfo;
        this.cacheCallback = cacheCallback;
    }

    @Override
    public void run() {
        MysqlConnection connection = new MysqlConnection(authInfo);
        try {
            connection.connect();
            connection.waitConnect();
            List<Map<Column, String>> list = MysqlUtils.query(connection.getChannel(), "show DATABASES");
            logger.info("Caching schema info...{}/{}", 0, list.size() - 3);
            int c = 0;
            Map<String,LinkedList<String>> tables = new HashMap<String, LinkedList<String>>();
            for (int i = 0; i < list.size(); i++) {
                Map<Column, String> row = list.get(i);
                boolean isContinue = false;
                for (Map.Entry<Column, String> entry : row.entrySet()) {
                    if (entry.getValue().equalsIgnoreCase(INFORMATION_SCHEMA)
                            || entry.getValue().equalsIgnoreCase(MYSQL)
                            || entry.getValue().equalsIgnoreCase(PERFORMANCE_SCHEMA)) {
                        isContinue = true;
                        break;
                    }
                    String schema = entry.getValue();
                    List<Map<Column, String>> tablesInfos = MysqlUtils.query(connection.getChannel(), "show tables from " + schema);
                    for (Map<Column, String> tableInfo : tablesInfos) {
                        for (Map.Entry<Column, String> columnStringEntry : tableInfo.entrySet()) {
                            String tableName = columnStringEntry.getValue();
                            List<Map<Column, String>> columnInfos = MysqlUtils.query(connection.getChannel(), "show columns from " + schema + "." + tableName);
                            LinkedList<String> fields = new LinkedList<String>();
                            for (Map<Column, String> columnInfo : columnInfos) {
                                for (Map.Entry<Column, String> column : columnInfo.entrySet()) {
                                    if (column.getKey().getName().equals("Field")) {
                                        fields.add(column.getValue());
                                    }
                                }
                            }
                            tables.put(schema.concat(".").concat(tableName), fields);
                        }
                    }
                }
                if (!isContinue) {
                    logger.info("Caching schema info {}/{}", ++c, list.size() - 3);
                }
            }
            logger.info("Cache schema successed!");
            connection.close();
            if(cacheCallback != null)cacheCallback.callback(tables);
        } catch (IOException e) {
            logger.info("Cache schema error!");
        }
    }

}
