package com.opdar.gulosity.connection.parser;

import java.nio.ByteBuffer;

/**
 * Created by Shey on 2016/8/21.
 */
public interface Parser<T> {
    T parser(ByteBuffer buffer);
}
