package com.yanwu.spring.cloud.common.utils;

import lombok.Getter;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

/**
 * @author Baofeng Xu
 * @date 2020/7/23 12:01.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class DateUtil {
    /*** 分隔符 ***/
    public static final String DASHES = "-", COLON = ":", SPACE = " ";
    /*** 中国时区 ***/
    private static final ZoneOffset UTC_8 = ZoneOffset.ofHours(8);

    private DateUtil() {
        throw new UnsupportedOperationException("DateUtil should never be instantiated");
    }

    /**
     * 根据时间格式字符串获取时间戳
     *
     * @param time   时间日期
     * @param format 格式
     * @return 时间戳
     */
    public static Long toTimeLong(String time, DateFormat format) throws Exception {
        return new SimpleDateFormat(format.getFormat()).parse(time).getTime();
    }

    /**
     * 根据时间戳和格式获取时间字符串
     *
     * @param time   时间戳
     * @param format 格式
     * @return 时间日期
     */
    public static String toTimeStr(Long time, DateFormat format) {
        return toTimeStr(new Date(time), format);
    }

    /**
     * 根据时间戳和格式获取时间字符串
     *
     * @param time   时间
     * @param format 格式
     * @return 时间日期
     */
    public static String toTimeStr(Date time, DateFormat format) {
        return new SimpleDateFormat(format.getFormat()).format(time);
    }

    /**
     * 时间戳转日期时间
     *
     * @param time 时间戳
     * @return 日期时间
     */
    public static LocalDateTime datetime(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), UTC_8);
    }

    /**
     * 日期时间转时间戳
     *
     * @param time 日期时间
     * @return 时间戳
     */
    public static Long datetime(LocalDateTime time) {
        return time.toInstant(UTC_8).toEpochMilli();
    }

    /**
     * 获取指定时间的字符串格式日期
     *
     * @param date 指定时间
     * @return 日期
     */
    public static String dateStr(LocalDate date) {
        return dateStr(date, DASHES);
    }

    /**
     * 获取指定时间的字符串格式日期
     *
     * @return 日期
     */
    public static String dateStr(LocalDate date, String delimiter) {
        return String.join(delimiter, String.valueOf(date.getYear()), filling(date.getMonthValue()), filling(date.getDayOfMonth()));
    }

    /**
     * 获取指定时间的字符串格式时间
     *
     * @return 时间
     */
    public static String timeStr(LocalTime time) {
        return timeStr(time, COLON);
    }

    /**
     * 获取指定时间的字符串格式时间
     *
     * @return 时间
     */
    public static String timeStr(LocalTime time, String delimiter) {
        return String.join(delimiter, filling(time.getHour()), filling(time.getMinute()), filling(time.getSecond()));
    }

    /**
     * 获取指定时间的字符串格式日期时间
     *
     * @return 日期时间
     */
    public static String datetimeStr(LocalDateTime dateTime) {
        return String.join(SPACE, dateStr(dateTime.toLocalDate()), timeStr(dateTime.toLocalTime()));
    }

    /**
     * 字符长度填充: 当data小于10时, 首位补0
     */
    public static String filling(int data) {
        return data >= 10 ? String.valueOf(data) : "0" + data;
    }

    /**
     * 时间字符串格式:
     * yyyy-MM-dd HH:mm:ss:SSS
     */
    public static enum DateFormat {
        YYYY_MM("yyyy-MM"),
        YYYYMM("yyyyMM"),
        YYYY_MM_DD("yyyy-MM-dd"),
        YYYYMMDD("yyyyMMdd"),
        YYYYMMDDHH("yyyyMMddHH"),
        HH_MM("HH:mm"),
        HHMM("HH mm"),
        HH_MM_SS("HH:mm:ss"),
        HHMMSS("HH mm ss"),
        HH_MM_SS_SSS("HH:mm:ss:SSS"),
        HHMMSSSSS("HH mm ss SSS"),
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
        YYYYMMDDHHMMSS("yyyyMMddHHmmss"),
        YYYY_MM_DD_HH_MM_SS_SSS("yyyy-MM-dd HH:mm:ss:SSS"),
        DD_MM_YYYY_HH_MM_SS("dd/MM/yyyy HH:mm:ss"),
        ;

        @Getter
        private final String format;

        DateFormat(String format) {
            this.format = format;
        }
    }
}
