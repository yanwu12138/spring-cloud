package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a herf="mailto:188234565@qq.com">胡佳</a>
 * @version 1.0 Created on 2014-11-6 下午02:21:34
 */
@Slf4j
@SuppressWarnings("all")
public class ByteUtil {

    private ByteUtil() {
    }

    /**
     * 累加和校验: 每字节相加（16进）, 然后取后末两位
     *
     * @param souce
     * @return
     */
    public static byte[] verifyCodeByte(byte[] souce) {
        byte[] temp = new byte[1];
        int result = 0;
        for (byte b : souce) {
            result += b;
        }
        temp[0] = (byte) (result % 256);
        return temp;
    }

    /**
     * 将int转成长度为2的byte数组
     *
     * @param souce
     * @return
     */
    public static byte[] shortToByte(int souce) {
        byte[] result = new byte[2];
        result[0] = (byte) (souce >> 8);
        result[1] = (byte) (souce);
        return result;
    }

    /**
     * 字符串反转, 如:
     * abc -> cba
     *
     * @param str
     * @return
     */
    public static String reverseStr(String str) {
        int length = str.length();
        String reverse = "";
        for (int i = 0; i < length; i++) {
            reverse = str.charAt(i) + reverse;
        }
        return reverse;
    }

    /**
     * 按位异或
     *
     * @param souce
     * @return
     */
    public static byte[] xorHex(byte[] souce) {
        byte[] result = {0x00};
        for (int i = 0; i < souce.length; i++) {
            if (i == 0) {
                result[0] = souce[0];
            } else {
                result[0] = (byte) (souce[i] ^ result[0]);
            }
        }
        return result;
    }

    /**
     * 字符串转十六进制字节数组
     *
     * @param souce
     * @return
     */
    public static byte[] strToHexBytes(String souce) {
        byte[] arrB = souce.getBytes();
        int iLen = arrB.length;
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * byte数组转十六进制字符串
     *
     * @param arr
     * @return
     */
    public static String bytesToHexStr(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            String hex = Integer.toHexString(arr[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 字节数组转换十六进制数组输出
     *
     * @return
     */
    public static String bytesToHexStrPrint(byte[] arr) {
        String[] cs = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            String hex = Integer.toHexString(arr[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            cs[i] = "" + hex;
        }
        return Arrays.toString(cs).toUpperCase();
    }

    /**
     * 十六进制字符串转中文字符串
     *
     * @param str
     * @return
     */
    public static String hexToStringGbk(String str) {
        byte[] baKeyword = new byte[str.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                log.error("hexToStringGBK error, ", e);
                return "";
            }
        }
        try {
            str = new String(baKeyword, "GBK");
        } catch (Exception e1) {
            log.error("hexToStringGBK error, ", e1);
            return "";
        }
        return str;
    }

    /**
     * 中文字符转十六进制字符串
     *
     * @param str
     * @return
     */
    public static String gbkStringToHex(String str) {
        String result = "";
        try {
            result = bytesToHexStr(str.getBytes("GBK")).toUpperCase();
        } catch (Exception e) {
            log.error("gbkToHex error, ", e);
        } finally {
            return result;
        }
    }

    /**
     * 字符每2位取反
     *
     * @param hex
     * @return
     */
    public static String reverseHex(String hex) {
        // hex = hex.replaceAll("0*$",""); ----- 是否将后面的0去除
        StringBuilder sb = new StringBuilder(hex);
        int j = 0;
        StringBuilder newStr = new StringBuilder();
        for (int i = 0; i < sb.length() / 2; i++) {
            String b = sb.substring(sb.length() - j - 2, sb.length() - j);
            j = j + 2;
            newStr.append(b);
        }
        return newStr.toString();
    }

    /**
     * 十进制转十六进制
     *
     * @param souce
     * @return
     */
    public static String decimalToHex(int souce) {
        String tmpValue = Integer.toHexString(souce).toUpperCase();
        if (tmpValue.length() % 2 == 1) {
            tmpValue = "0" + tmpValue;
        }
        return tmpValue;
    }

    /**
     * 十六进制转十进制
     *
     * @param str
     * @return
     */
    public static int hexToDecimal(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Integer.parseInt(str, 16);
        } else {
            return 0;
        }
    }

    /**
     * 十六进制带符号位进制转换
     *
     * @param str   十六进制字符串
     * @param radix 要转换的进制值，如2,8,10,16
     * @return
     */
    public static String hexToDecimal(String str, int radix) {
        byte[] bytes = strToHexBytes(str);
        return convertDecimal(bytes, radix);
    }

    /**
     * 十六进制转成二进制
     *
     * @param str
     * @return
     */
    public static String hexToBinary(String str) {
        long l = Long.parseLong(str, 16);
        String binaryString = Long.toBinaryString(l);
        int shouldBinaryLen = str.length() * 4;
        StringBuffer addZero = new StringBuffer();
        int addZeroNum = shouldBinaryLen - binaryString.length();
        for (int i = 1; i <= addZeroNum; i++) {
            addZero.append("0");
        }
        return addZero.toString() + binaryString;
    }

    /**
     * 二进制转成十进制
     *
     * @param str
     * @return
     */
    public static String binaryToDecimal(String str) {
        // 转换为BigInteger类型
        BigInteger src = new BigInteger(str, 2);
        return src.toString();
    }

    /**
     * 带符号位进制转换
     *
     * @param arr   要转换的字节数组
     * @param radix 要转换的进制值，如2,8,10,16
     * @return
     */
    public static String convertDecimal(byte[] arr, int radix) {
        // 这里的1代表正数
        return new BigInteger(1, arr).toString(radix);
    }

    /**
     * int转byte数组
     *
     * @param value
     * @param len
     * @return
     */
    public static byte[] intToByte(int value, int len) {
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) ((value >> 8 * i) & 0xFF);
        }
        return result;
    }

    /**
     * java带负号号转换
     *
     * @param i
     * @return
     */
    public static byte intToByte(int i) {
        return (byte) (i & 0xFF);
    }

    /**
     * 二进制转byte
     *
     * @param list
     * @return
     */
    public static byte bitToByte(List<Integer> list) {
        byte n = 0;
        for (int i = 0; i < list.size(); i++) {
            if (1 == list.get(list.size() - i - 1)) {
                n = (byte) (n | (1 << i));
            }
        }
        return n;
    }

    /**
     * @param b   对应转换byte[]
     * @param len 需要转换的字节长度
     * @param seq 在byte[]里的字节索引
     * @return
     */
    public static int byteToInt(byte[] b, int len, int seq) {
        if (b == null) {
            return 0;
        }
        int result = 0;
        for (int i = 0; i < len; i++) {
            if (len > i) {
                result = (antonymyByte(b[seq + i]) << 8 * i) + result;
            }
        }
        return result;
    }

    /**
     * 因为java比特最高位是符号位，需要取字节反码
     *
     * @param b
     * @return
     */
    public static int antonymyByte(byte b) {
        if (b < 0) {
            b = (byte) ~b;
            return 255 - b;
        }
        return b;
    }

    /**
     * 和byteToInt方法一样，只是seq顺序反着解析
     *
     * @param b
     * @param len
     * @param seq
     * @return
     */
    public static int byteToIntUpsideDown(byte[] b, int len, int seq) {
        if (b == null) {
            return 0;
        }
        int result = 0;
        for (int i = len; i > 0; i--) {
            int value = antonymyByte(b[seq + i - 1]) << (len - i) * 8;
            result = value + result;
        }
        return result;
    }

    public static int convertDfhzx(String hex) {
        String ejz = hexToBinary(reverseHex(hex));
        String fh = ejz.substring(0, 1);
        String sjz = ejz.substring(1, ejz.length());
        int rtsValue = 0;
        if ("0".equals(fh)) {
            rtsValue = -Integer.valueOf(sjz, 2).intValue();
        } else {
            rtsValue = Integer.valueOf(sjz, 2).intValue();
        }
        return rtsValue;
    }

    public static String byteToGbk(byte[] arr) throws Exception {
        String str = bytesToHexStr(arr);
        if (StringUtils.isNotBlank(str)) {
            return hexToStringGbk(str);
        }
        return null;
    }

    /**
     * 字符转ASC
     *
     * @param str
     * @return
     */
    public static byte[] strToAscBytes(String str) {
        try {
            return str.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            log.error("strToAscBytes error, ", e);
        }
        return null;
    }

    /**
     * double类型保留两位小数
     *
     * @param d
     * @return
     */
    public static Double munDouble(Double d) {
        Double db = 0d;
        if (d != null) {
            DecimalFormat df = new DecimalFormat("########0.00");
            String result = df.format(d);
            db = Double.valueOf(result);
        }
        return db;
    }

    /**
     * 获取指令中某个指令自己数组
     * 指定指定开始值，和指令长度
     *
     * @param data
     * @param begin
     * @param length
     * @return
     */
    public static byte[] getPartCodeBytes(byte[] data, int begin, int length) {
        byte[] result = new byte[length];
        if (data == null || data.length == 0) {
            return null;
        }
        if (data.length < length + 1 || data.length < begin + 1) {
            throw new RuntimeException("参数错误，数据长度小于" + begin + "或" + length);
        }
        System.arraycopy(data, begin, result, 0, length);
        return result;
    }

    /**
     * 获取指令中某个指令字节数组
     * 指定开始值，和指令结束值
     *
     * @param data
     * @param begin
     * @param end
     * @return
     */
    public static byte[] getPartCodeByCut(byte[] data, int begin, int end) {
        if (end <= begin) {
            return null;
        }
        byte[] result = new byte[end - begin];
        if (data == null || data.length == 0) {
            return null;
        }
        for (int i = 0; i < end - begin; i++) {
            if ((i + begin) <= end) {
                result[i] = data[begin + i];
            }
        }
        return result;
    }

    /**
     * 将字节转换为十六进制字符串0xFF,0x0A
     *
     * @param s
     * @return
     */
    public static String byteToHexString(byte s) {
        String s1 = Integer.toHexString(s & 0xff);
        if (s1.length() == 1) {
            s1 = "0x0" + s1.toUpperCase();
        }
        if (s1.length() == 2) {
            s1 = "0x" + s1.toUpperCase();
        }
        return s1;
    }

    /**
     * 数据补0
     *
     * @param str
     * @param length
     * @return
     */
    public static String fill0(String str, int length) {
        if (StringUtils.isNotBlank(str)) {
            return str;
        }
        int strLen = str.length();
        if (strLen < length) {
            while (strLen < length) {
                StringBuffer sb = new StringBuffer();
                sb.append("0").append(str);
                str = sb.toString();
                strLen = str.length();
            }
            return str;
        }
        if (strLen == length) {
            return str;
        }
        if (strLen > length) {
            return str.substring(strLen - length, strLen);
        }
        return str;
    }

}
