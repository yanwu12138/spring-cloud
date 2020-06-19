package com.yanwu.spring.cloud.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * @author <a herf="mailto:yanwu0527@163.com">yanwu</a>
 * @date 2019-08-26 14:22.
 * <p>
 * description:
 * 本方法使用CRC-16/MODBUS算法
 */
@SuppressWarnings("all")
public class Crc16Util {
    private static final Integer ONE = 1;
    private static final Integer TWO = 2;
    private static final Integer HEX = 16;
    private static final String NUL = "";
    private static final String SPACE = " ";
    private static final String ASCII = "US-ASCII";
    private static final String[] SPLITS = {SPACE, "\t", "\n", "\n"};

    /**
     * 根据报文byte数组，获取CRC-16 16进制字符串<p>
     * 48 4C 01 00 01 00 00 05 00 00 >> 0xE647
     *
     * @param data 报文数组
     * @return CRC值（16进制）
     */
    public static String getCrc16HexStr(String data) {
        return intToHexStr(getCrc16(data));
    }

    /**
     * 根据报文byte数组，获取CRC-16 int值<p>
     * 48 4C 01 00 01 00 00 05 00 00 >> 58951
     *
     * @param data 报文数组
     * @return CRC值（10进制）
     */
    public static int getCrc16(String data) {
        if (StringUtils.isBlank(data)) {
            // ----- 校验：报文字符串不能为空，否则抛异常
            throw new RuntimeException("The string cannot be empty!");
        }
        return getCrc16(getByteArr(data));
    }

    /**
     * 根据报文byte数组，获取CRC-16 16进制字符串<p>
     * {0x48, 0x4C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x00} >> 0xE647
     *
     * @param data 报文数组
     * @return CRC值（16进制）
     */
    public static String getCrc16HexStr(byte[] data) {
        return intToHexStr(getCrc16(data));
    }

    /**
     * 根据报文byte数组，获取CRC-16 int值<p>
     * {0x48, 0x4C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x00} >> 58951
     *
     * @param data 报文数组
     * @return CRC值（10进制）
     */
    public static int getCrc16(byte[] data) {
        if (data.length == 0) {
            // ----- 校验：报文数组不能为空，否则抛异常
            throw new RuntimeException("The array cannot be empty!");
        }
        // ----- 预置一个CRC寄存器，初始值为0xFFFF
        int crc = 0xFFFF;
        byte byteLen = 8;
        for (byte b : data) {
            // ----- 循环，将每数据帧中的每个字节与CRC寄存器中的低字节进行异或运算
            crc ^= ((int) b & 0x00FF);
            for (int j = 0; j < byteLen; j++) {
                // ----- 将寄存器右移1位，最高位自动补0
                if ((crc & 0x0001) != 0) {
                    // ----- 如果右移出来的位不为0，将寄存器与固定值 0xA001 异或运算
                    crc >>= 1;
                    crc ^= 0xA001;
                } else {
                    // ----- 如果右移出来的位为0，不做处理，进行下一次右移，知道处理完整个字节的8位
                    crc >>= 1;
                }
            }
        }
        // ----- 最终寄存器得值就是CRC的值，返回
        return crc;
    }

    /**
     * 将16进制字符串转换为16进制Byte数组<p>
     * 48 4C 01 00 01 00 00 05 00 00 >> {0x48, 0x4C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x00}
     *
     * @param str 报文字符串
     * @return 报文数组
     */
    private static byte[] getByteArr(String str) {
        str = str.replaceAll(SPACE, NUL);
        int strLen = str.length();
        if ((strLen & ONE) == ONE) {
            // ----- 报文字符串必须是以一个字节为单位（两位为一个字节），所以当去除空格后的报文长度为单数时说明报文错误
            throw new RuntimeException("Incorrect message format!");
        }
        byte[] result = new byte[strLen / TWO];
        // ----- 两位一个字节
        for (int i = 0; i < strLen; i += TWO) {
            String temp = str.substring(i, i + TWO);
            result[i / TWO] = (byte) Integer.parseInt(temp, HEX);
        }
        return result;
    }

    /**
     * 将CRC-16值转换成16进制字符串，且保持最小长度为4位<p>
     * 58951 >> E647
     *
     * @param data CRC值（10进制）
     * @return CRC值（16进制）
     */
    private static String intToHexStr(int data) {
        String crcStr = Integer.toHexString(data).toUpperCase();
        int size = 4 - crcStr.length();
        StringBuilder builder = new StringBuilder();
        // ---- 长度不够 4 位高位自动补0
        while (size > 0) {
            builder.append("0");
            size--;
        }
        return buffer.append(crcStr).toString();
    }

    /**
     * 去除字符串中的 【空格、\r、\n】 等字符
     *
     * @param str
     * @return
     */
    private static String processingString(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        for (String split : SPLITS) {
            if (StringUtils.isBlank(str)) {
                break;
            }
            if (str.contains(split)) {
                str = str.replaceAll(split, NUL);
            }
        }
        return str;
    }

    /**
     * 输出16进制与长度, 提供给 C++ CRC校验方法 测试 代码使用
     *
     * @param str 16进制字符串
     */
    private static void printHexStr(String str) {
        String[] split = str.split(SPACE);
        StringBuilder builder = new StringBuilder();
        builder.append("    unsigned char arr[] = {");
        for (int i = 0; i < split.length; i++) {
            builder.append("0x").append(split[i]);
            if (i < split.length - 1) {
                builder.append(", ");
            }
        }
        builder.append("};");
        System.out.println(builder.toString());
        System.out.println("    int len = " + split.length + ";");
        System.out.println("    int len = " + convertHighLow(crc16ToHexStr(split.length)) + ";");
    }

    /**
     * 输出json字符串与长度, 提供给 C++ CRC校验方法 测试 代码使用
     *
     * @param str json字符串
     */
    private static void printJsonStr(String str) {
        str = Crc16Util.processingString(str);
        System.out.println("    str: " + str.length() + " -> " + str);
        Character[] escapes = {'\"', '\'', '\\', '\b', '\n', '\r', '\t'};
        char[] chars = str.toCharArray();
        StringBuffer buffer = new StringBuffer();
        buffer.append("    unsigned char arr[] = {");
        for (int i = 0; i < chars.length; i++) {
            char item = chars[i];
            buffer.append("\'");
            for (char c : escapes) {
                if (item == c) {
                    buffer.append("\\");
                    break;
                }
            }
            buffer.append(item).append("\'");
            if (i < chars.length - 1) {
                buffer.append(", ");
            }
        }
        buffer.append("};");
        System.out.println(buffer.toString());
        System.out.println("    int len = " + chars.length + ";");
        System.out.println("    int len = " + convertHighLow(crc16ToHexStr(chars.length)) + ";");
    }

    /**
     * 测试CRC获取
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String param = "{\n" +
                "    \"ver\": \"010005\",\n" +
                "    \"mcode\": \"set\",\n" +
                "    \"ccode\": \"setAdaptiveLevel\",\n" +
                "    \"data\": {\n" +
                "        \"status\": 0\n" +
                "    },\n" +
                "    \"errorCode\": \"00\",\n" +
                "    \"seq\": \"6520C4A215CD471AA1654535CDF723D5\"\n" +
                "}\n";
        printJsonStr(param);
        System.out.println(getCrc16HexStrByJson(param));
    }

}