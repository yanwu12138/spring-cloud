package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.Contents;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.regex.Pattern;

/**
 * @author Baofeng Xu
 * @date 2020/8/17 17:24.
 * <p>
 * description:
 */
@SuppressWarnings("unused")
public class IpUtil {
    private static final int IPV4_SLICES_LEN = 4;
    private static final Pattern IPV4_PATTERN = Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");
    private static final Pattern IPV6_PATTERN = Pattern.compile("^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$");

    /**
     * 将IPV4地址转换成一个int值
     *
     * @param ip IP串
     * @return int值
     */
    public static int ipv4ToInt(String ip) {
        Assert.isTrue(checkIpv4(ip), "The IP format is incorrect.");
        String[] ipSlices = ip.split("\\.");
        int result = 0;
        for (int i = 0; i < ipSlices.length; i++) {
            // ----- 将ip的每一段解析为int，并根据位置左移8位, 然后进行或运算
            result |= (Integer.parseInt(ipSlices[i]) << 8 * i);
        }
        return result;
    }

    /**
     * 将一个int值转换成IPV4地址
     *
     * @param ip int值
     * @return IP串
     */
    public static String intToIpv4(int ip) {
        String[] ipString = new String[IPV4_SLICES_LEN];
        for (int i = 0; i < IPV4_SLICES_LEN; i++) {
            // 每8位为一段，这里取当前要处理的最高位的位置
            int pos = i * 8;
            // 取当前处理的ip段的值
            int and = ip & (255 << pos);
            // 将当前ip段转换为0~255的数字，注意这里必须使用无符号右移
            ipString[i] = String.valueOf(and >>> pos);
        }
        return String.join(Contents.POINT, ipString);
    }

    /**
     * 将IPV4地址转换成一个long值
     *
     * @param ip IPV4串
     * @return long值
     */
    public static long ipv4ToLong(String ip) {
        Assert.isTrue(checkIpv4(ip), "The IP format is incorrect.");
        String[] splits = ip.split("\\.");
        long result = 0L;
        for (String str : splits) {
            result = (result << 8) ^ Integer.parseInt(str);
        }
        return result;
    }

    /**
     * 将一个long值转换成IPV4地址
     *
     * @param ip long值
     * @return IPV4串
     */
    public static String longToIpv4(long ip) {
        Assert.isTrue((ip >= 0 && ip <= 256 * 256 * 256 * 256L), "The IP format is incorrect.");
        long ip1 = ip >>> 24;
        long ip2 = (ip >>> 16) ^ (ip1 << 8);
        long ip3 = (ip >>> 8) ^ (ip2 << 8) ^ (ip1 << 16);
        long ip4 = ip ^ (ip3 << 8) ^ (ip2 << 16) ^ (ip1 << 24);
        return String.join(Contents.POINT, String.valueOf(ip1), String.valueOf(ip2), String.valueOf(ip3), String.valueOf(ip4));
    }

    /**
     * 校验IPV4地址
     *
     * @param ip IP地址
     * @return true: 合法; false: 不合法
     */
    public static boolean checkIpv4(String ip) {
        return StringUtils.isNotBlank(ip) && IPV4_PATTERN.matcher(ip).find();
    }

    /**
     * 校验IPV6地址
     *
     * @param ip IP地址
     * @return true: 合法; false: 不合法
     */
    public static boolean checkIpv6(String ip) {
        return StringUtils.isNotBlank(ip) && IPV6_PATTERN.matcher(ip).find();
    }

}
