package com.yanwu.spring.cloud.common.utils;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author <a herf="mailto:yanwu0527@163.com">yanwu</a>
 * @date 2019-08-26 14:22.
 * <p>
 * description:
 * 本方法使用CRC-16/MODBUS算法
 */
@SuppressWarnings("unused")
public class Crc16Util {
    private static final Integer ONE = 1;
    private static final String NUL = "";
    private static final String SPACE = " ";
    private static final String[] SPLITS = {SPACE, "\t", "\r", "\n"};

    private Crc16Util() {
        throw new UnsupportedOperationException("Crc16Util should never be instantiated");
    }

    /**
     * 根据报文json对象，获取CRC-16 16进制字符串<p/>
     * {"code":"setServerAddr","data":{"type":2,"inAddr":"www.hoolink.com"},"sn":"light001","type":"light","version":"010101","seq":6}
     * >> 0xB84B
     *
     * @param json json对象
     * @return CRC值（16进制）
     */
    public static <T> String getCrc16HexStrByJson(T json) {
        return crc16ToHexStr(getCrc16ByJson(json));
    }

    /**
     * 根据报文json对象，获取CRC-16 int值<p/>
     * "{\"code\":\"setServerAddr\",\"data\":{\"type\":2,\"inAddr\":\"www.hoolink.com\"},\"sn\":\"light001\",\"type\":\"light\",\"version\":\"010101\",\"seq\":6}"
     * >> 47179
     *
     * @param json json对象
     * @return CRC值（10进制）
     */
    public static <T> int getCrc16ByJson(T json) {
        Assert.isTrue(Objects.nonNull(json), "The object cannot be empty!");
        return getCrc16ByJson(JsonUtil.toJsonString(json));
    }

    /**
     * 根据报文json字符串，获取CRC-16 16进制字符串<p/>
     * "{\"code\":\"setServerAddr\",\"data\":{\"type\":2,\"inAddr\":\"www.cloud-hoolink.com\"},\"sn\":\"1001126417824781234\",\"type\":\"light\",\"version\":\"010101\",\"seq\":62235}"
     * >> 0xB84B
     *
     * @param json json字符串
     * @return CRC值（16进制）
     */
    public static String getCrc16HexStrByJson(String json) {
        return crc16ToHexStr(getCrc16ByJson(json));
    }

    /**
     * 根据报文json对象字符串，获取CRC-16 int值<p/>
     * "{\"code\":\"setServerAddr\",\"data\":{\"type\":2,\"inAddr\":\"www.cloud-hoolink.com\"},\"sn\":\"1001126417824781234\",\"type\":\"light\",\"version\":\"010101\",\"seq\":62235}"
     * >> 47179
     *
     * @param json json字符串
     * @return CRC值（10进制）
     */
    public static int getCrc16ByJson(String json) {
        json = processingString(json);
        Assert.isTrue(StringUtils.isNotBlank(json), "The string cannot be empty!");
        // ===== 所有的字符必须属于ASCII码
        Assert.isTrue(CharMatcher.ascii().matchesAllOf(json), "All characters must belong to ASCII code!");
        return getCrc16ByHex(json.getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 根据报文byte数组，获取CRC-16 16进制字符串<p/>
     * 48 4C 01 00 01 00 00 05 00 00 >> 0xE647
     *
     * @param source 报文数组
     * @return CRC值（16进制）
     */
    public static String getCrc16HexStrByHex(String source) {
        return crc16ToHexStr(getCrc16ByHex(source));
    }

    /**
     * 根据报文byte数组，获取CRC-16 int值<p/>
     * 48 4C 01 00 01 00 00 05 00 00 >> 58951
     *
     * @param source 报文数组
     * @return CRC值（10进制）
     */
    public static int getCrc16ByHex(String source) {
        Assert.isTrue(StringUtils.isNotBlank(source), "The string cannot be empty!");
        return getCrc16ByHex(ByteUtil.hexStrToBytes(source));
    }

    /**
     * 根据报文byte数组，获取CRC-16 16进制字符串<p/>
     * {0x48, 0x4C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x00} >> 0xE647
     *
     * @param source 报文数组
     * @return CRC值（16进制）
     */
    public static String getCrc16HexStrByHex(byte[] source) {
        return crc16ToHexStr(getCrc16ByHex(source));
    }

    /**
     * 根据报文byte数组，获取CRC-16 int值<p/>
     * {0x48, 0x4C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x00} >> 58951
     *
     * @param source 报文数组
     * @return CRC值（10进制）
     */
    public static int getCrc16ByHex(byte[] source) {
        if (source.length == 0) {
            // ----- 校验：报文数组不能为空，否则抛异常
            throw new RuntimeException("The array cannot be empty!");
        }
        // ----- 预置一个CRC寄存器，初始值为0xFFFF
        int crc = 0xFFFF;
        byte byteLen;
        boolean flag;
        for (byte item : source) {
            // ----- 循环，将每数据帧中的每个字节与CRC寄存器中的低字节进行异或运算
            crc ^= ((int) item & 0x00FF);
            byteLen = 8;
            while (byteLen > 0) {
                // ----- 判断寄存器最后一位是 1\0：[true: 1; false: 0]
                flag = (crc & ONE) == ONE;
                // ----- 将寄存器右移1位，最高位自动补0
                crc >>= 1;
                if (flag) {
                    // ----- 如果右移出来的位是 1：将寄存器与固定值 0xA001 异或运算
                    // ----- 如果右移出来的位是 0：不做处理，进行下一次右移
                    // ----- 直到处理完整个字节的8位
                    crc ^= 0xA001;
                }
                byteLen--;
            }
        }
        // ----- 最终寄存器得值就是CRC的值，返回
        return crc;
    }

    /**
     * 将CRC-16值转换成16进制字符串，且保持最小长度为4位<p/>
     * 58951 >> E647
     *
     * @param data CRC值（10进制）
     * @return CRC值（16进制）
     */
    public static String crc16ToHexStr(int data) {
        String crcStr = Integer.toHexString(data).toUpperCase();
        int size = 4 - crcStr.length();
        StringBuilder builder = new StringBuilder();
        // ---- 长度不够 4 位高位自动补0
        while (size > 0) {
            builder.append("0");
            size--;
        }
        return builder.append(crcStr).toString();
    }

    /**
     * 去除字符串中的 【空格、\r、\n】 等字符
     *
     * @param jsonStr json字符串
     * @return 去除后的字符串
     */
    private static String processingString(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        // ----- 在计算CRC之前去除字符串中的特殊字符
        for (String split : SPLITS) {
            if (jsonStr.contains(split)) {
                jsonStr = jsonStr.replaceAll(split, NUL);
            }
        }
        return jsonStr;
    }

}