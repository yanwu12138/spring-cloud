package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.enums.PackageAnalyzeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Baofeng Xu
 * @date 2021/6/11 16:56.
 * <p>
 * description: 做粘包和半包的处理
 */
@Slf4j
public class PackageUtil {

    /***
     * 半包的缓存
     * KEY: netty的通道ID
     * VALUE: 通道ID对应的半包缓存
     */
    public static final Map<String, ByteBuf> PACKAGE_CACHE = new ConcurrentHashMap<>();

    /**
     * 根据 analyzeEnum 做粘包、半包处理
     *
     * @param channelId   通道号
     * @param bytes       数据包
     * @param analyzeEnum 解析方式
     * @return 解析出来的完整包内容
     * @see com.yanwu.spring.cloud.netty.enums.PackageAnalyzeEnum
     */
    public static List<byte[]> reader(String channelId, byte[] bytes, PackageAnalyzeEnum analyzeEnum) {
        if (StringUtils.isBlank(channelId) || analyzeEnum == null) {
            return Collections.emptyList();
        }
        // ----- 读取缓存中的数据包
        bytes = readCache(channelId, bytes);
        if (ArrayUtils.isEmpty(bytes)) {
            return Collections.emptyList();
        }
        if (!analyzeEnum.isLength() && !analyzeEnum.isTail()) {
            // ----- 该协议类型仅有帧头标识符
            return readByHead(channelId, bytes, analyzeEnum.getHead());
        }
        // ----- 根据协议是否包含帧头来选择不同的组包处理
        return Collections.emptyList();
    }

    public static void main(String[] args) {
        List<byte[]> bytes1 = reader("11111", new byte[]{(byte) 0xAA, (byte) 0xFF, (byte) 0x01}, PackageAnalyzeEnum.TYPE_1);
        bytes1.forEach(bytes -> log.info("bytes1: {}", ByteUtil.printBytes(bytes)));

        List<byte[]> bytes2 = reader("11111", new byte[]{0x04, 0x05, 0x06}, PackageAnalyzeEnum.TYPE_1);
        bytes2.forEach(bytes -> log.info("bytes2: {}", ByteUtil.printBytes(bytes)));

        List<byte[]> bytes3 = reader("11111", new byte[]{0x07, 0x08, 0x09, (byte) 0xAA}, PackageAnalyzeEnum.TYPE_1);
        bytes3.forEach(bytes -> log.info("bytes3: {}", ByteUtil.printBytes(bytes)));

        List<byte[]> bytes4 = reader("11111", new byte[]{(byte) 0xFF, 0x03, 0x04, 0x05}, PackageAnalyzeEnum.TYPE_1);
        bytes4.forEach(bytes -> log.info("bytes4: {}", ByteUtil.printBytes(bytes)));

        List<byte[]> bytes5 = reader("11111", new byte[]{(byte) 0xAA, (byte) 0xFF, 0x03, 0x04}, PackageAnalyzeEnum.TYPE_1);
        bytes5.forEach(bytes -> log.info("bytes5: {}", ByteUtil.printBytes(bytes)));
    }

    /**
     * 处理仅有帧头作为标识的数据
     *
     * @param bytes 报文
     * @param head  帧头
     * @return 数据包
     */
    private static List<byte[]> readByHead(String channelId, byte[] bytes, byte[] head) {
        if (bytes.length < head.length) {
            writeCache(channelId, bytes);
            return Collections.emptyList();
        }
        // ----- 将完整（两个帧头之间）的报文解析出来
        List<byte[]> result = new ArrayList<>();
        int index = 0, headNum = 0, begin = 0, end = -1;
        while (index < bytes.length - head.length) {
            byte[] headTemp = new byte[head.length];
            System.arraycopy(bytes, index, headTemp, 0, headTemp.length);
            if (Objects.deepEquals(head, headTemp)) {
                // ----- 说明找到了帧头
                if (headNum == 0) {
                    begin = index - head.length;
                }
                if (headNum == 1) {
                    byte[] dataTemp = new byte[index - begin];
                    System.arraycopy(bytes, begin, dataTemp, 0, dataTemp.length);
                    result.add(dataTemp);
                    end = index;
                }
                headNum++;
            }
            index++;
        }
        if (end > 0) {
            // ----- 将剩下不完整的报文缓存起来
            byte[] surplus = new byte[bytes.length - end];
            System.arraycopy(bytes, end, surplus, 0, surplus.length);
            writeCache(channelId, surplus);
        }
        return result;
    }

    /**
     * 读取缓存中的数据包
     *
     * @param channelId 通道ID
     * @param bytes     本次的数据包
     * @return 缓存中的数据包和本次接收的数据包组包后的结果
     */
    private synchronized static byte[] readCache(String channelId, byte[] bytes) {
        byte[] cacheBytes;
        if (!PACKAGE_CACHE.containsKey(channelId)) {
            PACKAGE_CACHE.put(channelId, Unpooled.compositeBuffer());
            return bytes;
        } else {
            ByteBuf byteBuf = PACKAGE_CACHE.get(channelId);
            cacheBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(cacheBytes);
            byteBuf.clear();
        }
        // ----- 将缓存的数据包和接收的数据包组包
        return ArrayUtils.isEmpty(cacheBytes) ? bytes : ByteUtil.merge(cacheBytes, bytes);
    }

    /**
     * 将数据包放到缓存中
     *
     * @param channelId 通道ID
     * @param bytes     数据包
     */
    private synchronized static void writeCache(String channelId, byte[] bytes) {
        ByteBuf byteBuf;
        if (PACKAGE_CACHE.containsKey(channelId)) {
            byteBuf = PACKAGE_CACHE.get(channelId);
        } else {
            byteBuf = Unpooled.compositeBuffer();
        }
        byteBuf.writeBytes(bytes);
        PACKAGE_CACHE.put(channelId, byteBuf);
    }

}
