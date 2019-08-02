package com.yanwu.spring.cloud.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTimeUtil {

    public static final String Format_HourAndMinute = "HH:mm";
    public static final String Format_HourAndMinuteSecond = "HH:mm:ss";

    public static final String Format_Date = "yyyy-MM-dd";
    public static final String Format_FullDateTime = "yyyy-MM-dd HH:mm:ss.SSS";

    public static String formatToString(Date date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static long convertStringToMillisecond(String strTime, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(strTime);
        return date.getTime();

    }

    public static String convertMillisecondToString(long lTime, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(lTime);
        return sdf.format(date);
    }

}
