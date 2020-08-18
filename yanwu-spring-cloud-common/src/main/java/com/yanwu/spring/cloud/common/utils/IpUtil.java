package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.Contents;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * @author Baofeng Xu
 * @date 2020/8/17 17:24.
 * <p>
 * description:
 */
@SuppressWarnings("unused")
public class IpUtil {
    private static final long MIN_VALUE = 0L;
    private static final String MIN_IP = "0.0.0.0";
    private static final long MAX_VALUE = 4294967295L;
    private static final String MAX_IP = "255.255.255.255";

    /**
     * 将IP地址转换成一个long值
     *
     * @param ip IP串
     * @return int值
     */
    public static long ipToLong(String ip) {
        Assert.isTrue(StringUtils.isNotBlank(ip), "the IP is null.");
        if (MIN_IP.equals(ip) || MAX_IP.equals(ip)) {
            return MIN_IP.equals(ip) ? MIN_VALUE : MAX_VALUE;
        }
        String[] splits = ip.split("\\.");
        Assert.isTrue((splits.length == 4), "The IP format is incorrect.");
        long result = 0L;
        for (String str : splits) {
            int anInt = Integer.parseInt(str);
            Assert.isTrue((anInt >= 0 && anInt < 256), "The IP format is incorrect.");
            result = (result << 8) ^ anInt;
        }
        return result;
    }

    /**
     * 将一个long值转换成IP地址
     *
     * @param ip int值
     * @return IP串
     */
    public static String longToIp(long ip) {
        Assert.isTrue((ip >= MIN_VALUE && ip <= MAX_VALUE), "The IP value is incorrect.");
        if (ip == MIN_VALUE || ip == MAX_VALUE) {
            return ip == MIN_VALUE ? MIN_IP : MAX_IP;
        }
        long ip1 = ip >>> 24;
        long ip2 = (ip >>> 16) ^ (ip1 << 8);
        long ip3 = (ip >>> 8) ^ (ip2 << 8) ^ (ip1 << 16);
        long ip4 = ip ^ (ip3 << 8) ^ (ip2 << 16) ^ (ip1 << 24);
        return ip1 + Contents.POINT + ip2 + Contents.POINT + ip3 + Contents.POINT + ip4;
    }

}
