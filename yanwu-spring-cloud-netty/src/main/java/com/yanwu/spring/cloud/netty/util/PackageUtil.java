package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.netty.enums.PackageAnalyzeEnum;
import io.netty.buffer.ByteBuf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Baofeng Xu
 * @date 2021/6/11 16:56.
 * <p>
 * description: 做粘包和半包的处理
 */
public class PackageUtil {

    /***
     * 半包的缓存
     * KEY: netty的通道ID
     * VALUE: 通道ID对应的半包缓存
     */
    public static final Map<String, ByteBuf> PACKAGE_CACHE = new ConcurrentHashMap<>();

    /**
     * 报文中只有帧头和帧尾的标识符，没有报文长度的处理方式
     * 注：如果报文中有长度，则建议使用长度进行拆包，该处理方式可能会导致以下问题
     * * 问题：报文中刚好有段内容为：帧尾 + 帧头
     * ** 如帧头为：0xAA, 0xBB；帧尾为：0xFF, 0xEE
     * ** 当报文为：{0xAA, 0xBB, 0x01, 0x02, 0xFF, 0xEE, 0xAA, 0xBB, 0x33, 0xFF, 0xEE} 时
     * ** 可能会将该报文拆解成：{0xAA, 0xBB, 0x01, 0x02, 0xFF, 0xEE} && {0xAA, 0xBB, 0x33, 0xFF, 0xEE} 这两个报文处理
     *
     * @param channelId   通道号
     * @param bytes       数据包
     * @param analyzeEnum 解析方式
     * @return 解析出来的完整包内容
     */
    public static byte[] reader(String channelId, byte[] bytes, PackageAnalyzeEnum analyzeEnum) {

        return new byte[]{};
    }

    /**
     * 校验帧头
     *
     * @param data 原始数据
     * @param head 帧头
     * @return [true: 以帧头开始; false: 不以帧头开始]
     */
    private static boolean isHead(byte[] data, byte[] head) {
        return false;
    }

    /**
     * 在原始报文中找帧尾
     *
     * @param data 原始数据
     * @param head 帧头
     * @param tail 帧尾
     * @return 帧尾的位置，当返回结果为-1时，说明改报文中没有帧尾
     */
    private static int findTail(byte[] data, byte[] head, byte[] tail) {
        return -1;
    }


    public static void main(String[] args) {
    }
}
