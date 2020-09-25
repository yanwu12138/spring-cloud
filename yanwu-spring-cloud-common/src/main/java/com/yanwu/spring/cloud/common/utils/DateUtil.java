package com.yanwu.spring.cloud.common.utils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;

/**
 * @author Baofeng Xu
 * @date 2020/7/23 12:01.
 * <p>
 * description:
 */
@SuppressWarnings("unused")
public class DateUtil {
    /*** 分隔符 ***/
    private static final String DASHES = "-", COLON = ":", SPACE = " ";
    /*** 中国时区 ***/
    private static final ZoneOffset UTC_8 = ZoneOffset.ofHours(8);

    private DateUtil() {
    }

    /**
     * 时间日期的前一年
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeYear(Long datetime) {
        return beforeYear(datetime(datetime));
    }

    /**
     * 时间日期的前一年
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeYear(LocalDateTime datetime) {
        return datetime.minusYears(1L);
    }

    /**
     * 时间日期的后一年
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterYear(Long datetime) {
        return afterYear(datetime(datetime));
    }

    /**
     * 时间日期的后一年
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterYear(LocalDateTime datetime) {
        return datetime.plusYears(1L);
    }


    /**
     * 时间日期的前一个月
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeMonth(Long datetime) {
        return beforeMonth(datetime(datetime));
    }


    /**
     * 时间日期的前一个月
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeMonth(LocalDateTime datetime) {
        return datetime.minusMonths(1L);
    }


    /**
     * 时间日期的后一个月
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterMonth(Long datetime) {
        return afterMonth(datetime(datetime));
    }


    /**
     * 时间日期的后一个月
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterMonth(LocalDateTime datetime) {
        return datetime.plusMonths(1L);
    }


    /**
     * 时间日期的前一天
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeDay(Long datetime) {
        return beforeDay(datetime(datetime));
    }

    /**
     * 时间日期的前一天
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeDay(LocalDateTime datetime) {
        return datetime.minusDays(1L);
    }

    /**
     * 时间日期的后一天
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterDay(Long datetime) {
        return afterDay(datetime(datetime));
    }

    /**
     * 时间日期的后一天
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterDay(LocalDateTime datetime) {
        return datetime.plusDays(1L);
    }

    /**
     * 时间日期的前一周
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeWeek(Long datetime) {
        return beforeWeek(datetime(datetime));
    }

    /**
     * 时间日期的前一周
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeWeek(LocalDateTime datetime) {
        return datetime.minusWeeks(1L);
    }

    /**
     * 时间日期的后一周
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterWeek(Long datetime) {
        return afterWeek(datetime(datetime));
    }

    /**
     * 时间日期的后一周
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterWeek(LocalDateTime datetime) {
        return datetime.plusWeeks(1L);
    }

    /**
     * 时间日期的前一个小时
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeHour(Long datetime) {
        return beforeHour(datetime(datetime));
    }

    /**
     * 时间日期的前一个小时
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime beforeHour(LocalDateTime datetime) {
        return datetime.minusHours(1L);
    }

    /**
     * 时间日期的后一个小时
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterHour(Long datetime) {
        return afterHour(datetime(datetime));
    }

    /**
     * 时间日期的后一个小时
     *
     * @param datetime 时间日期
     * @return 时间戳
     */
    public static LocalDateTime afterHour(LocalDateTime datetime) {
        return datetime.plusHours(1L);
    }

    /**
     * 根据时间格式字符串获取时间戳
     *
     * @param time 时间日期
     * @param type 格式
     * @return 时间戳
     */
    public static Long toTimeLong(String time, String type) throws Exception {
        return new SimpleDateFormat(type).parse(time).getTime();
    }

    /**
     * 根据时间戳和格式获取时间字符串
     *
     * @param time 时间戳
     * @param type 格式
     * @return 时间日期
     */
    public static String toTimeStr(Long time, String type) {
        return new SimpleDateFormat(type).format(new Date(time));
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
        return date.getYear() + delimiter + filling(date.getMonthValue()) + delimiter + filling(date.getDayOfMonth());
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
        return filling(time.getHour()) + delimiter + filling(time.getMinute()) + delimiter + filling(time.getSecond());
    }

    /**
     * 获取指定时间的字符串格式日期时间
     *
     * @return 日期时间
     */
    public static String datetimeStr(LocalDateTime dateTime) {
        return dateStr(dateTime.toLocalDate()) + SPACE + timeStr(dateTime.toLocalTime());
    }

    /**
     * 字符长度填充: 当data小于10时, 首位补0
     *
     * @param data .
     * @return .
     */
    public static String filling(int data) {
        return data >= 10 ? String.valueOf(data) : "0" + data;
    }
}
