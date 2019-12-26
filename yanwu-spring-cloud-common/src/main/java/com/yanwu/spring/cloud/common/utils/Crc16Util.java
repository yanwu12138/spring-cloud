package com.yanwu.spring.cloud.common.utils;

import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-26 14:22.
 * <p/>
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
    private static final String RETURN = "\r";
    private static final String NEW_LINE = "\n";
    private static final String ASCII = "US-ASCII";

    private Crc16Util() {
    }

    /**
     * 将16进制字符串进行高低位转换  <p/>
     * 0xE647 >> 0x47E6         <p/>
     * 0x484C01 >> 0x014C48     <p/>
     * 0x722E696D >> 0x6D692E72
     *
     * @return
     */
    public static String convertHighLow(String str) {
        Assert.isTrue(StringUtils.isNotBlank(str), "The string cannot be empty!");
        str = Crc16Util.processingString(str);
        int strLen = str.length();
        // ----- 报文字符串必须是以一个字节为单位（两个字符为一个字节），所以当去除所有空格后的字符串为单数时说明字符串错误
        Assert.isTrue(((strLen & ONE) != ONE), "Incorrect String format!");
        StringBuffer buffer = new StringBuffer();
        for (int i = strLen; i > 0; i -= TWO) {
            buffer.append(str.substring(i - TWO, i));
        }
        return buffer.toString();
    }

    /**
     * 根据报文json对象，获取CRC-16 16进制字符串<p/>
     * {"code":"setServerAddr","data":{"type":2,"inAddr":"www.hoolink.com"},"sn":"light001","type":"light","version":"010101","seq":6}
     * >> 0xB84B
     *
     * @param data json对象
     * @return CRC值（16进制）
     */
    public static <T> String getCrc16HexStrByJson(T data) throws Exception {
        return crc16ToHexStr(getCrc16ByJson(data));
    }

    /**
     * 根据报文json对象，获取CRC-16 int值<p/>
     * "{\"code\":\"setServerAddr\",\"data\":{\"type\":2,\"inAddr\":\"www.hoolink.com\"},\"sn\":\"light001\",\"type\":\"light\",\"version\":\"010101\",\"seq\":6}"
     * >> 47179
     *
     * @param data json对象
     * @return CRC值（10进制）
     */
    public static <T> int getCrc16ByJson(T data) throws Exception {
        Assert.isTrue(Objects.nonNull(data), "The data cannot be empty!");
        return getCrc16ByJson(JsonUtil.toJsonString(data));
    }

    /**
     * 根据报文json字符串，获取CRC-16 16进制字符串<p/>
     * "{\"code\":\"setServerAddr\",\"data\":{\"type\":2,\"inAddr\":\"www.cloud-hoolink.com\"},\"sn\":\"1001126417824781234\",\"type\":\"light\",\"version\":\"010101\",\"seq\":62235}"
     * >> 0xB84B
     *
     * @param data json字符串
     * @return CRC值（16进制）
     */
    public static String getCrc16HexStrByJson(String data) throws Exception {
        return crc16ToHexStr(getCrc16ByJson(data));
    }

    /**
     * 根据报文json对象字符串，获取CRC-16 int值<p/>
     * "{\"code\":\"setServerAddr\",\"data\":{\"type\":2,\"inAddr\":\"www.cloud-hoolink.com\"},\"sn\":\"1001126417824781234\",\"type\":\"light\",\"version\":\"010101\",\"seq\":62235}"
     * >> 47179
     *
     * @param data json字符串
     * @return CRC值（10进制）
     */
    public static int getCrc16ByJson(String data) throws Exception {
        Assert.isTrue(StringUtils.isNotBlank(data), "The string cannot be empty!");
        data = Crc16Util.processingString(data);
        // ===== 所有的字符必须属于ASCII码
        Assert.isTrue(CharMatcher.ascii().matchesAllOf(data), "All characters must belong to ASCII code!");
        return getCrc16ByHex(data.getBytes(ASCII));
    }

    /**
     * 根据报文byte数组，获取CRC-16 16进制字符串<p/>
     * 48 4C 01 00 01 00 00 05 00 00 >> 0xE647
     *
     * @param data 报文数组
     * @return CRC值（16进制）
     */
    public static String getCrc16HexStrByHex(String data) {
        return crc16ToHexStr(getCrc16ByHex(data));
    }

    /**
     * 根据报文byte数组，获取CRC-16 int值<p/>
     * 48 4C 01 00 01 00 00 05 00 00 >> 58951
     *
     * @param data 报文数组
     * @return CRC值（10进制）
     */
    public static int getCrc16ByHex(String data) {
        // ----- 校验：报文字符串不能为空，否则抛异常
        Assert.isTrue(StringUtils.isNotBlank(data), "The string cannot be empty!");
        return getCrc16ByHex(hexStrToByteArr(data));
    }

    /**
     * 根据报文byte数组，获取CRC-16 16进制字符串<p/>
     * {0x48, 0x4C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x00} >> 0xE647
     *
     * @param data 报文数组
     * @return CRC值（16进制）
     */
    public static String getCrc16HexStrByHex(byte[] data) {
        return crc16ToHexStr(getCrc16ByHex(data));
    }

    /**
     * 根据报文byte数组，获取CRC-16 int值<p/>
     * {0x48, 0x4C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x00} >> 58951
     *
     * @param data 报文数组
     * @return CRC值（10进制）
     */
    public static int getCrc16ByHex(byte[] data) {
        // ----- 校验：报文数组不能为空，否则抛异常
        Assert.isTrue((data.length > 0), "The array cannot be empty!");
        // ----- 预置一个CRC寄存器，初始值为0xFFFF
        int crc = 0xFFFF;
        byte byteLen;
        boolean flag;
        for (byte item : data) {
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
     * 将16进制字符串转换为16进制Byte数组<p/>
     * 48 4C 01 00 01 00 00 05 00 00 >> {0x48, 0x4C, 0x01, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x00}
     *
     * @param str 报文字符串
     * @return 报文数组
     */
    public static byte[] hexStrToByteArr(String str) {
        str = Crc16Util.processingString(str);
        int strLen = str.length();
        // ----- 报文字符串必须是以一个字节为单位（两个字符为一个字节），所以当去除所有空格后的报文长度为单数时说明报文错误
        Assert.isTrue(((strLen & ONE) != ONE), "Incorrect message format!");
        byte[] result = new byte[strLen / TWO];
        // ----- 两位一个字节
        for (int i = 0; i < strLen; i += TWO) {
            String temp = str.substring(i, i + TWO);
            result[i / TWO] = (byte) Integer.parseInt(temp, HEX);
        }
        return result;
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
        StringBuffer buffer = new StringBuffer();
        // ---- 长度不够 4 位高位自动补0
        while (size > 0) {
            buffer.append("0");
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
        if (str.contains(SPACE)) {
            str = str.replaceAll(SPACE, NUL);
        }
        if (str.contains(RETURN)) {
            str = str.replaceAll(RETURN, NUL);
        }
        if (str.contains(NEW_LINE)) {
            str = str.replaceAll(NEW_LINE, NUL);
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
        System.out.println("    str: " + split.length + " -> " + str);
        StringBuffer buffer = new StringBuffer();
        buffer.append("    unsigned char arr[] = {");
        for (int i = 0; i < split.length; i++) {
            buffer.append("0x").append(split[i]);
            if (i < split.length - 1) {
                buffer.append(", ");
            }
        }
        buffer.append("};");
        System.out.println(buffer.toString());
        System.out.println("    int len = " + split.length + ";");
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
    }

    /**
     * 测试CRC获取
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // ===== 测试1：hex方式获取CRC-16
        String str = "48 4C 01 00 01 00 00 05 00 00";
        // ----- 输出16进制数组给 C++ 测试使用
        Crc16Util.printHexStr(str);
        // ----- 获取CRC-16的值
        System.out.println("hex to crc16 int is: " + Crc16Util.getCrc16ByHex(str));
        System.out.println("hex to crc16 hex is: " + Crc16Util.getCrc16HexStrByHex(str));
        System.out.println();

        // ===== 测试2：json方式获取CRC-16
        Map param = new HashMap<>();
        param.put("version", "010001");
        param.put("type", "light");
        param.put("sn", "light001");
        param.put("seq", 1);
        param.put("code", "login");
        Crc16Util.printJsonStr(JsonUtil.toJsonString(param));
        System.out.println("json to crc16 int is: " + Crc16Util.getCrc16ByJson(param));
        System.out.println("json to crc16 hex is: " + Crc16Util.getCrc16HexStrByJson(param));
        System.out.println();

        // ===== 测试3：将16进制字符串进行高低位转换
        String temp = "722E696D";
        String lowBits = Crc16Util.convertHighLow(temp);
        System.out.println(temp + " -> " + lowBits);
        System.out.println();

        // ===== 测试4：获取FTP地址的十六进制数组
        String ftp = "ftp://127.0.0.1/xxx-dt1.1-v1.1.2.2r.img";
        byte[] asc = ftp.getBytes(ASCII);
        System.out.println(ByteUtil.bytesToHexStrPrint(asc));
        System.out.println(Integer.toHexString(asc.length));
    }
}