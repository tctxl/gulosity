package com.opdar.gulosity.spring.annotations;

import java.lang.annotation.*;

/**
 * Created by 俊帆 on 2016/10/14.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TableField {
    String value();
}
