package com.yanwu.spring.cloud.common.utils;

import com.github.veqryn.net.Cidr4;
import com.yanwu.spring.cloud.common.core.common.Contents;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Baofeng Xu
 * @date 2020/8/17 17:24.
 * <p>
 * description: IP & MAC 工具类
 */
@SuppressWarnings("unused")
public class IpMacUtil {
    private static final int IPV4_SLICES_LEN = 4;
    private static final String UNKNOWN = "unknown";
    private static final String LOCAL_IPV4 = "127.0.0.1";
    private static final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";
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
     * 将一个IPV4串转换为一个IPV6串
     *
     * @param ip IPV4串
     * @return IPV6串
     */
    public static String ipv4ToIpv6(String ip) {
        Assert.isTrue(checkIpv4(ip), "The IP format is incorrect.");
        StringBuilder result = new StringBuilder("::");
        String[] ipSlices = ip.split("\\.");
        for (int i = 0; i < ipSlices.length; i++) {
            String ip1 = intToHexStr(Integer.parseInt(ipSlices[i]));
            String ip2 = intToHexStr(Integer.parseInt(ipSlices[++i]));
            result.append(ip1).append(ip2).append(":");
        }
        return result.toString().substring(0, result.length() - 1).toUpperCase();
    }

    /**
     * 将IPV6地址转换成一个long数组
     *
     * @param ip IPV6串
     * @return long[]
     */
    public static long[] ipv6ToLongArr(String ip) {
        Assert.isTrue(checkIpv6(ip), "The IP format is incorrect.");
        String[] ipSlices = ip.split(":");
        long[] result = new long[2];
        for (int i = 0; i < 8; i++) {
            String slice = ipSlices[i];
            // 以 16 进制解析
            long num = Long.parseLong(slice, 16);
            // 每组 16 位
            long right = num << (16 * i);
            // 每个 long 保存四组，i >> 2 = i / 4
            result[i >> 2] |= right;
        }
        return result;
    }

    /**
     * 将一个long数组转换成IPV6地址
     *
     * @param ip long[]
     * @return ipv6串
     */
    public static String longArrToIpv6(long[] ip) {
        Assert.isTrue((ip != null && ip.length == 2), "The IP format is incorrect.");
        StringBuilder sb = new StringBuilder(32);
        for (long ipSlice : ip) {
            // 每个 long 保存四组
            for (int j = 0; j < 4; j++) {
                // 取最后 16 位
                long current = ipSlice & 0xFFFF;
                sb.append(Long.toString(current, 16)).append(":");
                // 右移 16 位，即去除掉已经处理过的 16 位
                ipSlice >>= 16;
            }
        }
        // 去掉最后的 : 号
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 校验IPV4地址
     *
     * @param ip IPV4地址
     * @return true: 合法; false: 不合法
     */
    public static boolean checkIpv4(String ip) {
        return checkIp(ip, IPV4_PATTERN);
    }

    /**
     * 校验IPV6地址
     *
     * @param ip IPV6地址
     * @return true: 合法; false: 不合法
     */
    public static boolean checkIpv6(String ip) {
        return checkIp(ip, IPV6_PATTERN);
    }

    /**
     * 通过request获取用户的IP地址
     *
     * @param request 请求
     * @return IP地址
     */
    public static String getIpByRequest(HttpServletRequest request) throws Exception {
        String ipAddress;
        ipAddress = request.getHeader("client-origin-ip");
        if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCAL_IPV4.equals(ipAddress) || LOCAL_IPV6.equals(ipAddress)) {
                ipAddress = getLocalHostAddress().getHostAddress();
            }
        }
        // ----- 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (StringUtils.contains(ipAddress, ",")) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }
        return ipAddress;
    }


    /**
     * 获取本机IP地址
     *
     * @return IP
     * @throws Exception 未找到IP
     */
    public static String getLocalIp() throws Exception {
        return getLocalHostAddress().getHostAddress();
    }

    /**
     * 获取本机MAC地址
     *
     * @return MAC
     * @throws Exception Exception.class
     */
    public static String getLocalMac() throws Exception {
        // ----- 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
        InetAddress ia = getLocalHostAddress();
        byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
        String[] result = new String[mac.length];
        for (int i = 0; i < mac.length; i++) {
            // ----- mac[i] & 0xFF 是为了把byte转化为正整数
            result[i] = ByteUtil.headFill0(Integer.toHexString(mac[i] & 0xFF), 2);
        }
        return String.join(":", result).toUpperCase();
    }

    /**
     * 获取网卡名称
     *
     * @return 网卡
     * @throws Exception Exception.class
     */
    public static String getInterfaceName() throws Exception {
        NetworkInterface candidateInterface = null;
        Enumeration<NetworkInterface> enumerationArr = NetworkInterface.getNetworkInterfaces();
        while (enumerationArr.hasMoreElements()) {
            NetworkInterface networkInterface = enumerationArr.nextElement();
            Enumeration<InetAddress> addressArr = networkInterface.getInetAddresses();
            while (addressArr.hasMoreElements()) {
                InetAddress inetAddress = addressArr.nextElement();
                if (!inetAddress.isLoopbackAddress()) {
                    if (inetAddress.isSiteLocalAddress()) {
                        return networkInterface.getName();
                    } else {
                        candidateInterface = networkInterface;
                    }
                }
            }
        }
        return candidateInterface != null ? candidateInterface.getName() : "";
    }

    /**
     * 获取本机网卡配置
     *
     * @return 网卡配置
     * @throws Exception 未找到网卡配置
     */
    public static InetAddress getLocalHostAddress() throws Exception {
        try {
            InetAddress candidateAddress = null;
            // ----- 遍历所有的网络接口
            Enumeration<NetworkInterface> enumerations = NetworkInterface.getNetworkInterfaces();
            while (enumerations.hasMoreElements()) {
                Enumeration<InetAddress> addressArr = enumerations.nextElement().getInetAddresses();
                // ----- 在所有的接口下再遍历IP
                while (addressArr.hasMoreElements()) {
                    InetAddress address = addressArr.nextElement();
                    // ----- 排除loopback类型地址
                    if (!address.isLoopbackAddress()) {
                        if (address.isSiteLocalAddress()) {
                            // ----- 如果是site-local地址，就是它了
                            return address;
                        } else if (candidateAddress == null) {
                            // ----- site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = address;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // ----- 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            throw new UnknownHostException("Failed to determine LAN address: " + e);
        }
    }

    /**
     * 校验IP地址是否合法
     *
     * @param ip      IP地址
     * @param pattern 正则表达式
     * @return true: 合法; false: 不合法
     */
    private static boolean checkIp(String ip, Pattern pattern) {
        return StringUtils.isNotBlank(ip) && pattern.matcher(ip).find();
    }

    /**
     * 将一个int值转换成16进制字符串
     *
     * @param number int
     * @return hexStr
     */
    private static String intToHexStr(int number) {
        Assert.isTrue((number >= 0 && number < 256), "The IP format is incorrect.");
        StringBuilder result = new StringBuilder(Integer.toHexString(number));
        if (result.length() < 2) {
            result.insert(0, "0");
        }
        return result.toString();
    }

    public static void main(String[] args) {
        Set<String> ips = new HashSet<>();
        ips.add("127.0.0.1");
        ips.add("156.164.356.101");
        ips.add("172.16.0.56/24");
        ips.add("121.111.146.12/11");
        String param = "172.16.0.56";
        System.out.println(check(ips, param));
    }

    private static boolean check(Set<String> ips, String param) {
        if (CollectionUtils.isEmpty(ips)) {
            return false;
        }
        if (ips.contains(param)) {
            return true;
        }
        for (String ip : ips) {
            if (param.equals(ip) || checkIpByMask(ip, param)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkIpByMask(String ip1, String ip2) {
        if (StringUtils.isBlank(ip1) || StringUtils.isBlank(ip2)) {
            return false;
        }
        if (ip1.contains("/")) {
            Cidr4 cidr1 = new Cidr4(ip1);
            if (ip2.contains("/")) {
                Cidr4 cidr2 = new Cidr4(ip2);
            } else {
                return cidr1.isInRange(ip2, Boolean.TRUE);
            }
        } else {
            if (ip2.contains("/")) {
                Cidr4 cidr2 = new Cidr4(ip2);
                return cidr2.isInRange(ip1, Boolean.TRUE);
            }
        }
        return false;
    }

}