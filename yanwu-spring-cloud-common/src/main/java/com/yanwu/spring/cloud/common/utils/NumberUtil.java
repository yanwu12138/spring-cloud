package com.yanwu.spring.cloud.common.utils;

import java.math.BigInteger;
import java.util.Random;

/**
 * @author Baofeng Xu
 * @date 2020/9/15 17:30.
 * <p>
 * description: 数值型工具类
 */
@SuppressWarnings("unused")
public class NumberUtil {
    public static final Random RANDOM = new Random();

    private NumberUtil() {
    }

    /**
     * 二进制转八进制
     *
     * @param binStr 二进制
     * @return 八进制
     */
    public static String binStrToOctStr(String binStr) {
        return Long.toOctalString(binStrToDecimal(binStr));
    }

    /**
     * 八进制转二进制
     *
     * @param octStr 八进制
     * @return 二进制
     */
    public static String octStrToBinStr(String octStr) {
        return decimalToBinStr(getLongByRadix(octStr, 8));
    }

    /**
     * 二进制转十进制
     *
     * @param binStr 二进制
     * @return 十进制
     */
    public static long binStrToDecimal(String binStr) {
        return getLongByRadix(binStr, 2);
    }

    /**
     * 十进制转二进制
     *
     * @param decimal 十进制
     * @return 二进制
     */
    public static String decimalToBinStr(long decimal) {
        return Long.toBinaryString(decimal);
    }

    /**
     * 二进制转十六进制
     *
     * @param binStr 二进制
     * @return 十六进制
     */
    public static String binStrToHexStr(String binStr) {
        return Long.toHexString(binStrToDecimal(binStr)).toUpperCase();
    }

    /**
     * 十六进制转二进制
     *
     * @param hexStr 十六进制
     * @return 二进制
     */
    public static String hexStrToBinStr(String hexStr) {
        return decimalToBinStr(getLongByRadix(hexStr, 16));
    }

    /**
     * 八进制转十进制
     *
     * @param octStr 八进制
     * @return 十进制
     */
    public static long octStrToDecimal(String octStr) {
        return binStrToDecimal(octStrToBinStr(octStr));
    }

    /**
     * 十进制转八进制
     *
     * @param decimal 十进制
     * @return 八进制
     */
    public static String decimalToOctStr(long decimal) {
        return binStrToOctStr(decimalToBinStr(decimal));
    }

    /**
     * 八进制转十六进制
     *
     * @param octStr 八进制
     * @return 十六进制
     */
    public static String octStrToHexStr(String octStr) {
        return binStrToHexStr(octStrToBinStr(octStr)).toUpperCase();
    }

    /**
     * 十六进制转八进制
     *
     * @param hexStr 十六进制
     * @return 八进制
     */
    public static String hexStrToOctStr(String hexStr) {
        return binStrToOctStr(hexStrToBinStr(hexStr));
    }

    /**
     * 十进制转十六进制
     *
     * @param decimal 十进制
     * @return 十六进制
     */
    public static String decimalToHexStr(long decimal) {
        return binStrToHexStr(decimalToBinStr(decimal)).toUpperCase();
    }

    /**
     * 十六进制转十进制
     *
     * @param hexStr 十六进制
     * @return 十进制
     */
    public static long hexStrToDecimal(String hexStr) {
        return binStrToDecimal(hexStrToBinStr(hexStr));
    }

    /**
     * 将指定进制数字字符串转为十进制数字
     *
     * @param source 源数据
     * @param radix  指定进制
     * @return 十进制
     */
    private static long getLongByRadix(String source, int radix) {
        return new BigInteger(source, radix).longValue();
    }

}
