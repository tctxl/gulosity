package test;

import com.opdar.gulosity.spring.listeners.DefaultAutoMappingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class TestListener extends DefaultAutoMappingListener {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public <T> void onInsert(T object) {
        logger.debug("新增数据：" + object);
    }

    @Override
    public <T> void onDelete(T object) {
        logger.debug("删除数据：" + object);
    }

    @Override
    public <T> void onUpdate(T before, T after) {
        logger.debug("更新前：" + before);
        logger.debug("更新后：" + after);
    }
}
