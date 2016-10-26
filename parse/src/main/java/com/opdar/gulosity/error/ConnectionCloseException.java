package com.opdar.gulosity.error;

/**
 * Created by Shey on 2016/8/22.
 */
public class ConnectionCloseException extends RuntimeException {
    public ConnectionCloseException(Throwable e) {
        super(e);
    }
}
