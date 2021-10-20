package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.enums.PackageAnalyzeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public static void main(String[] args) {
        PackageAnalyzeEnum[] values = PackageAnalyzeEnum.values();

    }

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
        if (analyzeEnum.isTail()) {
            if (analyzeEnum.isLength()) {
                // ----- 该协议类型为：TYPE_3（帧头 + 长度 + 帧尾）
                return reader03(channelId, bytes, analyzeEnum);
            } else {
                // ----- 该协议类型为：TYPE_2（帧头 + 帧尾）
                return reader02(channelId, bytes, analyzeEnum);
            }
        } else {
            // ----- 该协议类型为：TYPE_1（帧头 + 长度）
            return reader01(channelId, bytes, analyzeEnum);
        }
    }

    private static List<byte[]> reader01(String channelId, byte[] bytes, PackageAnalyzeEnum analyzeEnum) {
        bytes = readCache(channelId, bytes);

        return Collections.emptyList();
    }

    private static List<byte[]> reader02(String channelId, byte[] bytes, PackageAnalyzeEnum analyzeEnum) {
        bytes = readCache(channelId, bytes);
        return Collections.emptyList();
    }

    private static List<byte[]> reader03(String channelId, byte[] bytes, PackageAnalyzeEnum analyzeEnum) {
        bytes = readCache(channelId, bytes);
        return Collections.emptyList();
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
