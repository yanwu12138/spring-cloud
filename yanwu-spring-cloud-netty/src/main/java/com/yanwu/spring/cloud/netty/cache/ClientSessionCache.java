package com.yanwu.spring.cloud.netty.cache;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:25.
 * <p>
 * description: 设备通讯缓存
 */
@Slf4j
@Component
public final class ClientSessionCache {
    private static final Map<String, ChannelHandlerContext> TCP_SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<String, InetSocketAddress> UDP_SESSION_MAP = new ConcurrentHashMap<>();
    private static final Map<String, String> DEVICE_ONLINE_MAP = new ConcurrentHashMap<>();

    public void sessionSync() {
        try {
            log.info("Current connection number >> TCP: {}, UDP: {}", TCP_SESSION_MAP.size(), UDP_SESSION_MAP.size());
        } catch (Exception e) {
            log.error("[local cache] monitor has occur error, cause: " + e);
        }
    }

    /**
     * 缓存TCP通道
     *
     * @param ctxId   通道编号
     * @param channel 通道
     */
    public void putContext(String ctxId, ChannelHandlerContext channel) {
        if (Objects.nonNull(TCP_SESSION_MAP.get(ctxId))) {
            return;
        }
        TCP_SESSION_MAP.put(ctxId, channel);
    }

    /**
     * 根据通道编号获取TCP通道
     *
     * @param ctxId 通道编号
     * @return 通道
     */
    public ChannelHandlerContext getContext(String ctxId) {
        return TCP_SESSION_MAP.get(ctxId);
    }

    /**
     * 缓存UDP通讯
     *
     * @param address 地址
     * @param socket  通讯
     */
    public void putSocket(String address, InetSocketAddress socket) {
        if (Objects.nonNull(UDP_SESSION_MAP.get(address))) {
            return;
        }
        UDP_SESSION_MAP.put(address, socket);
    }

    /**
     * 根据地址获取UDP通道
     *
     * @param address 地址
     * @return UDP通讯
     */
    public InetSocketAddress getSocket(String address) {
        return UDP_SESSION_MAP.get(address);
    }

    /**
     * 根据KEY删除通道
     *
     * @param ctxId KEY
     */
    public void remove(String ctxId) {
        TCP_SESSION_MAP.remove(ctxId);
        UDP_SESSION_MAP.remove(ctxId);
    }

    public void putDevice(String sn, String ctxId) {
        DEVICE_ONLINE_MAP.put(sn, ctxId);
    }

    public String getDevice(String sn) {
        return DEVICE_ONLINE_MAP.get(sn);
    }

    public Set<String> getOnlines() {
        return DEVICE_ONLINE_MAP.keySet();
    }
}
