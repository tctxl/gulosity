package com.opdar.gulosity.base;

/**
 * Created by Shey on 2016/8/19.
 */
public class Constants {
    public static final String SHOW_MASTER_STATUS = "show master status";
    public static final String POSITION = "Position";
    public static final String FILE = "File";

    public static class MYSQL{
        public static final int HEADER_LENGTH = 4;
        public static final int EOF = (byte)0xfe;
        public static final int ERR_PACKET = (byte)0xff;
        public static final int EMPTY_PACKET = (byte)0xfb;

        public static final int FIXED_EVENT_LENGTH = 19;
    }
    public static class Event{
        public static final int ROTATE_EVENT = 0x04;
        public static final int FORMAT_DESCRIPTION_EVENT = 0x0f;
        public static final int QUERY_EVENT = 0x02;
        public static final int XID_EVENT = 0x10;
        public static final int TABLE_MAP_EVENT = 0x13;
        public static final int WRITE_ROWS_EVENTv1 = 0x17;
        public static final int UPDATE_ROWS_EVENTv1 = 0x18;
        public static final int DELETE_ROWS_EVENTv1 = 0x19;
        public static final int WRITE_ROWS_EVENTv2 = 0x1e;
        public static final int UPDATE_ROWS_EVENTv2 = 0x1f;
        public static final int DELETE_ROWS_EVENTv2 = 0x20;
        public static final int NEW_LOAD_EVENT = 12;
        public static final int RAND_EVENT = 13;
        public static final int USER_VAR_EVENT = 14;
        public static final int BEGIN_LOAD_QUERY_EVENT = 17;
        public static final int EXECUTE_LOAD_QUERY_EVENT = 18;
    }
}
