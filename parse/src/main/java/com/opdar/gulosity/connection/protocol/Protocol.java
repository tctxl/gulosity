package com.opdar.gulosity.connection.protocol;

/**
 * Created by Shey on 2016/8/19.
 */
public interface Protocol {
    void fromBytes(byte[] data);
    byte[] toBytes();
}
