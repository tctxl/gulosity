package com.opdar.gulosity.spring.listeners;

import com.opdar.gulosity.base.MysqlContext;
import com.opdar.gulosity.base.RowCallback;
import com.opdar.gulosity.entity.RowEntity;
import com.opdar.gulosity.persistence.IPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 俊帆 on 2016/10/14.
 */
public abstract class DefaultAutoMappingListener implements RowCallback {
    private Logger logger = LoggerFactory.getLogger(getClass());
    public static final Map<String,Class<?>> classes = new HashMap<String, Class<?>>();

    public static void putAllClasses(Map<? extends String, ? extends Class<?>> m) {
        classes.putAll(m);
    }

    public static Class<?> putClass(String key, Class<?> value) {
        return classes.put(key, value);
    }

    public void onNotify(RowEntity entity, RowEntity entity2) {
        if(classes.containsKey(entity.getTableName())){
            Class<?> clz = classes.get(entity.getTableName());
            Field[] fields = clz.getDeclaredFields();
            Object obj = instanceObject(entity, clz, fields);
            try{
                switch (entity.getEventType()) {
                    case WRITEV1:
                    case WRITEV2:
                        onInsert(obj);
                        break;
                    case DELETEV1:
                    case DELETEV2:
                        onDelete(obj);
                        break;
                    case UPDATEV1:
                    case UPDATEV2:
                        Object obj2 = instanceObject(entity2, clz, fields);
                        onUpdate(obj,obj2);
                        break;
                }
            }finally {
            }
        }
    }

    private Object instanceObject(RowEntity entity, Class<?> clz, Field[] fields) {
        Object obj = null;
        try {
            obj = clz.newInstance();
            for(Field field:fields){
                field.setAccessible(true);
                int index = entity.getColumnInfo().indexOf(field.getName());
                field.set(obj,entity.getResult()[index]);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public abstract <T>void onInsert(T object);
    public abstract <T>void onDelete(T object);
    public abstract <T>void onUpdate(T before,T after);
}