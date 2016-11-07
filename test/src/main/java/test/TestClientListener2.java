package test;

import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.spring.listeners.DefaultAutoMappingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClientListener2 implements RowCallback {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onNotify(RowEntity entity, RowEntity entity2) {
        logger.debug("更新前：" + entity);
        logger.debug("更新后：" + entity2);
    }
}
