package com.yanwu.spring.cloud.udp.server.util;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-24 16:29.
 * <p>
 * description:
 */
public class DeviceUtil {
    /**
     * 判断是否是报警灯报文
     *
     * @param bs
     * @return
     */
    public static boolean checkDevice(byte[] bs) {
        boolean headFlag = bs[0] == (byte) 0x48 && bs[1] == (byte) 0x4C;
        boolean endFlag = bs[bs.length - 1] == (byte) 0x48 && bs[bs.length - 2] == (byte) 0x4C;
        return headFlag && endFlag;
    }

    /**
     * 解析报警灯报文
     *
     * @param bs
     * @return
     */
    public static String getSn(byte[] bs) {
        byte[] bytes = new byte[bs.length - 4];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = bs[i + 2];
        }
        return new String(bytes);
    }

}
