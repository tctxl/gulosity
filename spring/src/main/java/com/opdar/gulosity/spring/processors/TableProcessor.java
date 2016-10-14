package com.opdar.gulosity.spring.processors;

import com.opdar.gulosity.spring.annotations.Table;
import com.opdar.gulosity.spring.listeners.DefaultAutoMappingListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Created by 俊帆 on 2016/10/14.
 */
public class TableProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Table table = AnnotationUtils.findAnnotation(bean.getClass(), Table.class);
        if(table !=null){
            DefaultAutoMappingListener.putClass(table.value(), bean.getClass());
        }
        return bean;
    }
}
