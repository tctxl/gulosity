package com.opdar.gulosity.spring.annotations;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by 俊帆 on 2016/10/14.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Table {
    String value();
    boolean camelCase() default false;
    public enum CaseExtension{
        NULL,UPPER,LOWER
    }
    CaseExtension extension() default CaseExtension.NULL;
}
