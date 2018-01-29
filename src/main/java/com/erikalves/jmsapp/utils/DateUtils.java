package com.erikalves.jmsapp.utils;

import java.sql.Timestamp;

public class DateUtils {


    public static java.sql.Date getCurrentDate() {

        return new java.sql.Date(System.currentTimeMillis());
    }


    public static Timestamp getCurrentTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        return timestamp;
    }

    public static Long getTimestampInMilliseconds(){
        return getTimestampInMilliseconds(null);
    }

    public static long getTimestampInMilliseconds(Timestamp ts){

        long timestampInMilliseconds = 0;

        if(null == ts) {
           timestampInMilliseconds = getCurrentTimestamp().getTime();
        }

        return timestampInMilliseconds;


    }


}
