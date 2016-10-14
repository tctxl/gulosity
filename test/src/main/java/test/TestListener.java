package test;

import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.entity.RowEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class TestListener implements RowCallback {
    private Logger logger = LoggerFactory.getLogger(getClass());
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
}
