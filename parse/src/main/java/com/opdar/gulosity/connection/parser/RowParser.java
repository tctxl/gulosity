package com.opdar.gulosity.connection.parser;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Shey on 2016/8/21.
 */
public class RowParser implements Parser<List<String>> {
    public List<String> parser(ByteBuffer buffer) {
        List<String> row = new LinkedList<String>();
        while (buffer.hasRemaining()){
            int length = buffer.get();
            if(length > 0){
                byte[] column = new byte[length];
                buffer.get(column);
                try {
                    row.add(new String(column,"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else{
                //0xfb == null
                row.add(null);
            }
        }
        return row;
    }
}
