package com.yanwu.spring.cloud.common.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

/**
 * @author Administrator
 */
public class RandomUtil {

    final static private Random random = new Random();

    public static Integer generateRandomInteger(int min, int max) {
        int bound = max - min;
        Assert.isTrue(bound >= 0, "Max must be greater than or equal to min");
        if (bound < Integer.MAX_VALUE) {
            ++bound;
        }
        return random.nextInt(bound) + min;
    }

    public static Short generateRandomShort(short min, short max) {
        int bound = max - min;
        Assert.isTrue(bound >= 0, "Max must be greater than or equal to min");
        Assert.isTrue(bound <= Short.MAX_VALUE, "Delta out of range");
        if (bound < Short.MAX_VALUE) {
            ++bound;
        }
        return (short) (random.nextInt(bound) + min);
    }

    public static Byte generateRandomByte(byte min, byte max) {
        int bound = max - min;
        Assert.isTrue(bound >= 0, "Max must be greater than or equal to min");
        Assert.isTrue(bound <= Byte.MAX_VALUE, "Delta out of range");
        if (bound < Byte.MAX_VALUE) {
            ++bound;
        }
        return (byte) (random.nextInt(bound) + min);
    }

    public static Long generateRandomLong(long min, long max) {
        return (long) (Math.random() * (max - min + 1)) + min;
    }

    public static Double generateRandomDouble() {
        return Math.random() * Integer.MAX_VALUE;
    }

    public static Float generateRandomFloat() {
        return (float) (Math.random() * Float.MAX_VALUE);
    }

    public static Boolean generateRandomBoolean() {
        return random.nextBoolean();
    }

    public static Character generateRandomCharacter() {
        return RandomStringUtils.randomAlphabetic(1).charAt(0);
    }

    public static String generateRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static Date generateRandomDate(long baseTime) {
        return new Date((long) (Math.random() * 86400000) + baseTime);
    }

    public static Date generateRandomDate() {
        return generateRandomDate(System.currentTimeMillis());
    }

    public static Timestamp generateRandomTimeStamp(long baseTime) {
        return new Timestamp(generateRandomDate(baseTime).getTime());
    }

    public static Timestamp generateRandomTimeStamp() {
        return new Timestamp(generateRandomDate().getTime());
    }
}
