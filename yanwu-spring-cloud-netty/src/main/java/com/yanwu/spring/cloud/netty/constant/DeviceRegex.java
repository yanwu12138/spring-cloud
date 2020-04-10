package com.yanwu.spring.cloud.netty.constant;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:17.
 * <p>
 * description:
 */
public class DeviceRegex {

    /*** 信息发布正则表达式 */
    public static final class Screen {
        /*** 帧头：1字节；设备编号：2字节；命令字：1字节；数据域N字节；帧尾：1字节；校验码2字节 */
        public final static String SCREEN_NOVA_REGEX = "(?<head>\\w{2})(?<deviceNo>\\w{4})(?<code>\\w{2})(?<data>\\w*)(?<end>\\w{2})(?<crc>\\w{4})";
    }

}
