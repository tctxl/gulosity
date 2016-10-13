package com.opdar.gulosity.error;

/**
 * Created by Shey on 2016/8/22.
 */
public class NotSupportBinlogException extends RuntimeException {
    public NotSupportBinlogException(String message) {
        super(message);
    }
}
