package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Baofeng Xu
 * @date 2020/9/15 17:30.
 * <p>
 * description: byte字节工具类
 */
@Slf4j
@SuppressWarnings("unused")
public class ByteUtil {

    private ByteUtil() {
    }

    /**
     * 将字节转换为十六进制字符串0xFF,0x0A
     *
     * @param source byte
     * @return hexStr
     */
    public static String byteToHexStr(byte source) {
        String result = Integer.toHexString(source & 0xff);
        result = headFill0(result, 2);
        result = "0x" + result;
        return result.toUpperCase();
    }

    /**
     * byte数组转16进制字符串
     *
     * @param bytes byte数组
     * @return 16进制字符串
     */
    public static String bytesToHexStr(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString((b & 0xFF));
            if (hex.length() == 1) {
                result.append('0');
            }
            result.append(hex);
        }
        return result.toString().toUpperCase();
    }

    /**
     * 16进制字符串转byte数组
     *
     * @param hexStr 16进制字符串
     * @return byte数组
     */
    public static byte[] hexStrToBytes(String hexStr) {
        int len = hexStr.length();
        byte[] result = new byte[len / 2];
        for (int i = 0; i < result.length; i = i + 2) {
            int temp = Integer.parseInt(hexStr.substring(i, i + 2), 16);
            byte b = (byte) (temp & 0xFF);
            result[i / 2] = b;
        }
        return result;
    }

    /**
     * 16进制字符串转16进制字节数组
     *
     * @param source 源数据
     * @return 转换结果
     */
    public static byte[] hexStrToHexBytes(String source) {
        byte[] result = new byte[source.length() / 2];
        for (int i = 0; i < source.length(); i = i + 2) {
            String strTmp = source.substring(i, i + 2);
            result[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return result;
    }

    /**
     * 将字节数组转换16进制数组输出格式
     *
     * @param bytes 字节数组
     * @return 输出格式
     */
    public static String printHexStrByBytes(byte[] bytes) {
        String[] cs = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            cs[i] = hex.length() == 1 ? "0" + hex : hex;
        }
        return Arrays.toString(cs).toUpperCase();
    }

    /**
     * byte数组转换成GBK字符串
     *
     * @param bytes 字节数组
     * @return gbkStr
     */
    public static String bytesToGbkStr(byte[] bytes) {
        String str = bytesToHexStr(bytes);
        if (StringUtils.isNotBlank(str)) {
            return hexStrToGbkStr(str);
        }
        return null;
    }


    /**
     * 16进制字符串转GBK字符串
     *
     * @param source 源数据
     * @return 转换结果
     */
    public static String hexStrToGbkStr(String source) {
        byte[] bytes = new byte[source.length() / 2];
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            try {
                bytes[i] = (byte) (0xFF & Integer.parseInt(source.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                log.error("hexToStringGbk error.", e);
                return result;
            }
        }
        try {
            result = new String(bytes, "GBK");
        } catch (Exception e1) {
            log.error("hexToStringGbk error.", e1);
        }
        return result;
    }

    /**
     * gbk字符串转16进制字符串
     *
     * @param source gbk字符串
     * @return 16进制字符串
     */
    public static String gbkStrToHexStr(String source) {
        String result = "";
        try {
            result = bytesToHexStr(source.getBytes("GBK"));
        } catch (Exception e) {
            log.error("gbkToHex error, ", e);
        }
        return result.toUpperCase();
    }

    /**
     * 字符串转ASCII码字节数组
     *
     * @param source 源字符串
     * @return ASCII字节数组
     */
    public static byte[] strToAsciiBytes(String source) {
        return source.getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * 累加和校验: 每字节相加（16进）, 然后取后末两位
     *
     * @param source 源数据
     * @return 校验结果
     */
    public static byte checkSummation(byte[] source) {
        int temp = 0;
        for (byte b : source) {
            temp += b;
        }
        return (byte) (temp % 256);
    }

    /**
     * 按位异或校验: 每个字节依次异或, 返回异或最终结果
     *
     * @param source 源数据
     * @return 异或结果
     */
    public static byte checkExclusive(byte[] source) {
        byte result = 0;
        for (byte b : source) {
            result = (byte) (b ^ result);
        }
        return result;
    }

    /**
     * 将字符串每两位进行取反
     *
     * @param source 源字符串
     * @return 取反后的字符串
     */
    public static String reverseHex(String source) {
        StringBuilder sb = new StringBuilder(source);
        int j = 0;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sb.length() / 2; i++) {
            String b = sb.substring(sb.length() - j - 2, sb.length() - j);
            j = j + 2;
            result.append(b);
        }
        return result.toString();
    }

    /**
     * 根据开始位置和结束位置截取字节数组
     * <p/>
     * * 注: 此函数结果包头不包尾
     *
     * @param bytes 源数据
     * @param begin 开始位置
     * @param end   结束位置
     * @return 截取后的字节数组
     */
    public static byte[] subBytesByEnd(byte[] bytes, int begin, int end) {
        if (begin < 0 || begin >= end) {
            return new byte[]{};
        }
        return subBytesByLen(bytes, begin, (end - begin));
    }

    /**
     * 根据开始位置和指定长度截取字节数组
     *
     * @param bytes  源数据
     * @param begin  开始位置
     * @param length 长度
     * @return 截取后的字节数组
     */
    public static byte[] subBytesByLen(byte[] bytes, int begin, int length) {
        if (bytes == null || bytes.length == 0 || bytes.length <= begin) {
            return new byte[]{};
        }
        byte[] result = new byte[length];
        System.arraycopy(bytes, begin, result, 0, length);
        return result;
    }

    /**
     * 根据指定长度在源字符串补0
     *
     * @param source 源字符串
     * @param length 最终长度
     * @return 补0后的字符串
     */
    public static String headFill0(String source, int length) {
        int strLen = source.length();
        if (strLen >= length) {
            return source;
        }
        StringBuilder builder = new StringBuilder(source);
        while (builder.length() < length) {
            builder.insert(0, "0");
        }
        return builder.toString();
    }


    /**
     * 将16进制字符串进行高低位转换  <p/>
     * 0xE647 >> 0x47E6         <p/>
     * 0x484C01 >> 0x014C48     <p/>
     * 0x722E696D >> 0x6D692E72
     *
     * @return 转换后的内容
     */
    public static String convertHighLow(String hexStr) {
        Assert.isTrue(StringUtils.isNotBlank(hexStr), "The string cannot be empty!");
        int strLen = hexStr.length();
        // ----- 报文字符串必须是以一个字节为单位（两个字符为一个字节），所以当去除所有空格后的字符串为单数时说明字符串错误
        Assert.isTrue(((strLen & 1) != 1), "Incorrect String format!");
        StringBuilder builder = new StringBuilder();
        for (int i = strLen; i > 0; i -= 2) {
            builder.append(hexStr, i - 2, i);
        }
        return builder.toString().toUpperCase();
    }

}
