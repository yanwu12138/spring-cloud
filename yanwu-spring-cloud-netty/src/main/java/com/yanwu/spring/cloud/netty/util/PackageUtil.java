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

        List<byte[]> bytes3 = reader("11111", new byte[]{0x07, 0x08, 0x09, 0x01}, PackageAnalyzeEnum.TYPE_1);
        bytes3.forEach(bytes -> log.info("bytes3: {}", ByteUtil.printBytes(bytes)));

        List<byte[]> bytes4 = reader("11111", new byte[]{0x02, 0x03, 0x04, 0x05}, PackageAnalyzeEnum.TYPE_1);
        bytes4.forEach(bytes -> log.info("bytes4: {}", ByteUtil.printBytes(bytes)));

        List<byte[]> bytes5 = reader("11111", new byte[]{0x01, 0x02, 0x03, 0x04}, PackageAnalyzeEnum.TYPE_1);
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
        while (checkHead(bytes, head)) {
            bytes = readByHead(bytes, head, result);
        }
        // ----- 将剩下不完整的报文缓存起来
        if (bytes.length > 0) {
            writeCache(channelId, bytes);
        }
        return result;
    }

    /**
     * 处理仅有帧头作为标识的数据
     *
     * @param bytes  报文
     * @param head   帧头
     * @param result 将完整的报文放入result做后续处理
     * @return 解析后剩下的数据
     */
    private static byte[] readByHead(byte[] bytes, byte[] head, List<byte[]> result) {
        int index = 0;
        byte[] tempBytes;
        // ----- 找到以帧头开始（包含帧头）的数据包
        while (index < bytes.length - head.length) {
            byte[] temp = new byte[head.length];
            System.arraycopy(bytes, index, temp, 0, temp.length);
            if (Objects.deepEquals(head, temp)) {
                // ----- 说明找到了帧头
                byte[] tempData = new byte[bytes.length - index];
                System.arraycopy(bytes, index, tempData, 0, tempData.length);
                result.add(tempData);
                tempBytes = new byte[bytes.length - tempData.length];
                System.arraycopy(bytes, tempData.length, tempBytes, 0, tempBytes.length);
                return tempBytes;
            }
            index++;
        }
        return bytes;
    }

    /**
     * 当协议只有帧头作为标识时，要解一个完整的包，需要取第一个帧头起始 到 第二个帧头之间的数据
     *
     * @param bytes 数据包
     * @param head  帧头标识
     * @return 是否可以解包【true: 可以; false: 不可以】
     */
    private static boolean checkHead(byte[] bytes, byte[] head) {
        int index = 0, headNum = 0;
        while (index < bytes.length - head.length) {
            byte[] temp = new byte[head.length];
            System.arraycopy(bytes, index, temp, 0, temp.length);
            if (Objects.deepEquals(head, temp)) {
                // ----- 说明找到了帧头
                headNum++;
                if (headNum >= 2) {
                    return true;
                }
            }
            index++;
        }
        return false;
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
