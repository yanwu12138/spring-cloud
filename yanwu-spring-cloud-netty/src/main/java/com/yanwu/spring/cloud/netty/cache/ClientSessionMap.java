package com.yanwu.spring.cloud.netty.cache;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:25.
 * <p>
 * description: 设备通讯缓存
 */
@Slf4j
public final class ClientSessionMap {

    private static final String CONTEXT_NAME = "tcp_context_local_cache";
    private static final String SOCKET_NAME = "udp_socket_local_cache";
    private static final Cache TCP_SESSION_MAP = CacheManager.create().getCache(CONTEXT_NAME);
    private static final Cache UDP_SESSION_MAP = CacheManager.create().getCache(SOCKET_NAME);

    public static void sessionSync() {
        try {
            int tcpSize = TCP_SESSION_MAP.getKeys().size();
            int udpSize = UDP_SESSION_MAP.getKeys().size();
            log.info("Current connection number >> TCP: {}, UDP: {}", tcpSize, udpSize);
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
    public static void putContext(String ctxId, ChannelHandlerContext channel) {
        if (Objects.nonNull(TCP_SESSION_MAP.get(ctxId))) {
            return;
        }
        TCP_SESSION_MAP.put(new Element(ctxId, channel));
    }

    /**
     * 根据通道编号获取TCP通道
     *
     * @param ctxId 通道编号
     * @return 通道
     */
    public static ChannelHandlerContext getContext(String ctxId) {
        Element element = TCP_SESSION_MAP.get(ctxId);
        return element == null ? null : (ChannelHandlerContext) element.getObjectValue();
    }

    /**
     * 缓存UDP通讯
     *
     * @param address 地址
     * @param socket  通讯
     */
    public static void putSocket(String address, InetSocketAddress socket) {
        if (Objects.nonNull(UDP_SESSION_MAP.get(address))) {
            return;
        }
        UDP_SESSION_MAP.put(new Element(address, socket));
    }

    /**
     * 根据地址获取UDP通道
     *
     * @param address 地址
     * @return UDP通讯
     */
    public static InetSocketAddress getSocket(String address) {
        Element element = UDP_SESSION_MAP.get(address);
        return element == null ? null : (InetSocketAddress) element.getObjectValue();
    }

    /**
     * 根据KEY删除通道
     *
     * @param ctxId KEY
     */
    public static void remove(String ctxId) {
        TCP_SESSION_MAP.remove(ctxId);
        UDP_SESSION_MAP.remove(ctxId);
    }
}
