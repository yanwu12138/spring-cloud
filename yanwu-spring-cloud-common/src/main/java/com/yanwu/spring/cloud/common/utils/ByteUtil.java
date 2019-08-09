package com.yanwu.spring.cloud.common.utils;

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
public class ByteUtil {

    /**
     * 累加和校验【每字节相加（16进）取后末两位】
     *
     * @param bytes
     * @return
     */
    public static byte[] verifyCodeByte(byte[] bytes) {
        byte[] temp = new byte[1];
        int result = 0;
        for (byte b : bytes) {
            result += b;
        }
        temp[0] = (byte) (result % 256);
        return temp;
    }

    /**
     * 将int转成byte[2]
     *
     * @param a
     * @return
     */
    public static byte[] short2Byte(int a) {
        byte[] b = new byte[2];
        b[0] = (byte) (a >> 8);
        b[1] = (byte) (a);

        return b;
    }

    /**
     * 字符串反转
     *
     * @param s
     * @return
     */
    public static String reverse(String s) {
        int length = s.length();
        String reverse = "";
        for (int i = 0; i < length; i++) {
            reverse = s.charAt(i) + reverse;
        }
        return reverse;
    }

    /**
     * 按位异或
     *
     * @param arr1
     * @return
     */
    public static byte[] xorHex(byte[] arr1) {
        byte[] arr2 = {0x00};
        for (int i = 0; i < arr1.length; i++) {
            if (i == 0) {
                arr2[0] = arr1[0];
            } else {
                arr2[0] = (byte) (arr1[i] ^ arr2[0]);
            }
        }
        return arr2;
    }

    /**
     * 字符串转数组
     *
     * @param strIn
     * @return
     */
    public static byte[] hexStr2ByteArr(String strIn) {
        byte[] arrB = strIn.getBytes();
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
     * 数组转16进制字符串
     *
     * @param arr
     * @return
     */
    public static String byteArr2HexStr(byte[] arr) {
        int iLen = arr.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arr[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        // 最大128位
        return sb.toString();
    }

    /**
     * 数组转16进制字符串
     *
     * @param arr
     * @return
     */
    public static String parseByte2HexStr(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++) {
            String hex = Integer.toHexString(arr[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 16进制转中文
     *
     * @param str
     * @return
     */
    public static String hexToStringGBK(String str) {
        byte[] baKeyword = new byte[str.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(str.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        try {
            // UTF-16le:Not
            str = new String(baKeyword, "GBK");
        } catch (Exception e1) {
            e1.printStackTrace();
            return "";
        }
        return str;
    }

    /**
     * 字符转16进制(含中文)
     *
     * @param str
     * @return
     */
    public static String gbkToHex(String str) {
        try {
            return byteArr2HexStr(str.getBytes("GBK")).toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 字符每2位取反
     *
     * @param hex
     * @return
     */
    public static String reverseHex(String hex) {
        // hex = hex.replaceAll("0*$","");//是否将后面的0去除
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
     * 10进制转16进制
     *
     * @param str
     * @return
     */
    public static String decimalToHex(int str) {
        String tmpValue = Integer.toHexString(str).toUpperCase();
        if (tmpValue.length() % 2 == 1) {
            tmpValue = "0" + tmpValue;
        }
        return tmpValue;
    }

    /**
     * 16进制转10进制
     *
     * @param str
     * @return
     */
    public static int hexToDecimal(String str) {
        if (str != null && !"".equals(str)) {
            return Integer.parseInt(str, 16);
        } else {
            return 0;
        }
    }

    /**
     * 16进制转成二进制
     *
     * @param str
     * @return
     */
    public static String convertHexToBinary(String str) {
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
    public static String convertBinaryToDecimal(String str) {
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
     * 16进制带符号位进制转换
     *
     * @param str   16进制字符串
     * @param radix 要转换的进制值，如2,8,10,16
     * @return
     */
    public static String convertHexToDecimal(String str, int radix) {
        byte[] bytes = hexStr2ByteArr(str);
        // 这里的1代表正数
        return convertDecimal(bytes, radix);
    }

    /**
     * 十六进制转换
     *
     * @param data
     * @param hex
     * @return
     */
    public static String jointHexData(String data, String hex) {
        if (hex.length() == 1) {
            hex = "0" + hex;
        }
        data = data + hex.toUpperCase() + ",";
        return data;
    }

    public static byte[] intToByte(int value, int len) {
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) ((value >> 8 * i) & 0xFF);
        }
        return result;
    }

    public static byte[] intToByte3(int i) {
        byte[] result = new byte[3];
        result[0] = (byte) (i & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        return result;
    }

    public static byte[] intToByte2(int i) {
        byte[] result = new byte[2];
        result[0] = (byte) (i & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
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
        String ejz = convertHexToBinary(reverseHex(hex));
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

    public static String byteToGBK(byte[] arr) throws Exception {
        String str = byteArr2HexStr(arr);
        if (StringUtils.isNotBlank(str)) {
            return hexToStringGBK(str);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字节数组转换十六进制数组输出
     *
     * @return
     */
    public static String bytesToHexPrint(byte[] arr) {
        String[] cs = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            String hex = Integer.toHexString(arr[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            cs[i] = "" + hex.toUpperCase();
        }
        return Arrays.toString(cs);
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
     * 将字节转换为16进制字符串0xFF,0x0A
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
