package com.opdar.gulosity.event.other;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.event.base.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public class SendCallbackEvent implements Event, Runnable {
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    private RowEntity entity1,entity2;

    private SendCallbackEvent(RowEntity rowEntity, RowEntity rowEntity2) {
        this.entity1 = rowEntity;
        this.entity2 = rowEntity2;
    }

    public static SendCallbackEvent create(RowEntity entity, RowEntity updateAfter) {
        return new SendCallbackEvent(entity,updateAfter);
    }

    public void doing() {
        executorService.execute(this);
    }

    public void run() {
        for (RowCallback rowCallback : MysqlContext.getRowCallbacks()) {
            try {
                rowCallback.onNotify(entity1,entity2);
            } catch (Exception e) {
                //notify $rowCallback failed
            }
        }
    }
}
