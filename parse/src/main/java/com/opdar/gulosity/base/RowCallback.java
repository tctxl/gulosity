package com.opdar.gulosity.base;

import com.opdar.gulosity.entity.RowEntity;

/**
 * Created by 俊帆 on 2016/10/13.
 */
public interface RowCallback {
    void onNotify(RowEntity entity, RowEntity entity2);
}
