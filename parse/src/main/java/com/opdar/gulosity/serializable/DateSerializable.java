package com.opdar.gulosity.serializable;

import com.opdar.gulosity.utils.BufferUtils;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shey on 2016/8/27.
 */
public class DateSerializable extends JavaSerializable<Date> {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public DateSerializable(int type) {
        super(type);
    }

    @Override
    public Date getValue(int meta, ByteBuffer buffer) {
        try {
            int date = (int) BufferUtils.readLong(buffer,3);
            if (date != 0) {
                Calendar calendar = Calendar.getInstance();
                int year = date / (16 * 32);
                int month = date / 32 % 16;
                int day = date % 32;
                if(year == 0){
                    year = calendar.get(Calendar.YEAR);
                }
                if(month == 0){
                    month = calendar.get(Calendar.MONTH);
                }
                if(day == 0){
                    day = calendar.get(Calendar.DATE);
                }
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month-1);
                calendar.set(Calendar.DATE,day);
                return new Date(calendar.getTimeInMillis());
            }
            return format.parse("0000-00-00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
