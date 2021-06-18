package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
        throw new UnsupportedOperationException("ByteUtil should never be instantiated");
    }

    /**
     * 将字节转换为十六进制字符串0xFF,0x0A
     *
     * @param source byte
     * @return hexStr
     */
    public static String byteToHexStr(byte source) {
        String result = Integer.toHexString(source & 0xFF);
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
            result.append(headFill0(hex, 2));
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
     * @param hexStr 源数据
     * @return 转换结果
     */
    public static byte[] hexStrToHexBytes(String hexStr) {
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length(); i = i + 2) {
            String strTmp = hexStr.substring(i, i + 2);
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
            cs[i] = headFill0(hex, 2);
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
     * 将字符串每两位进行取反  <p/>
     * * 0xE647 >> 0x47E6         <p/>
     * * 0x484C01 >> 0x014C48     <p/>
     * * 0x722E696D >> 0x6D692E72
     *
     * @param hexStr 源字符串
     * @return 取反后的字符串
     */
    public static String reverseHex(String hexStr) {
        if (StringUtils.isBlank(hexStr)) {
            return hexStr;
        }
        if (NumberUtil.odevity(hexStr.length())) {
            // ----- 报文字符串必须是以一个字节为单位（两个字符为一个字节），当长度为奇数时, 前面补0
            hexStr = headFill0(hexStr, hexStr.length() + 1);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = hexStr.length(); i > 0; i -= 2) {
            builder.append(hexStr, i - 2, i);
        }
        return builder.toString();
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
        if (StringUtils.isBlank(source)) {
            source = "";
        }
        if (source.length() >= length) {
            return source;
        }
        StringBuilder result = new StringBuilder(source);
        while (result.length() < length) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    /**
     * 将多个字节数组合并
     *
     * @param sources 字节数组
     * @return 合并后的字节数组
     */
    public static byte[] merge(byte[]... sources) {
        if (sources == null || sources.length == 0) {
            return new byte[]{};
        }
        if (sources.length == 1) {
            return sources[0];
        }
        byte[] result = new byte[0];
        for (byte[] bytes : sources) {
            if (bytes == null || bytes.length == 0) {
                continue;
            }
            byte[] temp = new byte[result.length + bytes.length];
            System.arraycopy(result, 0, temp, 0, result.length);
            System.arraycopy(bytes, 0, temp, result.length, bytes.length);
            result = temp;
        }
        return result;
    }

}
