package com.yanwu.spring.cloud.netty.cache;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.collections.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:25.
 * <p>
 * description:
 */
@Slf4j
public final class ClientSessionMap {

    private static final String CACHE_NAME = "ctxId_ctx_local_cache";
    private static final Cache SESSION_MAP = CacheManager.create().getCache(CACHE_NAME);

    public static void sessionSync() {
        try {
            List keys = SESSION_MAP.getKeys();
            int size = CollectionUtils.isEmpty(keys) ? 0 : keys.size();
            log.info("number of long connections currently detected: {}", size);
        } catch (Exception e) {
            log.error("[local cache] monitor has occur error, cause: " + e);
        }
    }

    public static void putContext(String ctxId, ChannelHandlerContext channel) {
        if (Objects.nonNull(SESSION_MAP.get(ctxId))) {
            return;
        }
        SESSION_MAP.put(new Element(ctxId, channel));
    }

    public static void putSocket(String address, InetSocketAddress socketAddress) {
        if (Objects.nonNull(SESSION_MAP.get(address))) {
            return;
        }
        SESSION_MAP.put(new Element(address, socketAddress));
    }

    public static ChannelHandlerContext getContext(String ctxId) {
        Element element = SESSION_MAP.get(ctxId);
        return element == null ? null : (ChannelHandlerContext) element.getObjectValue();
    }

    public static InetSocketAddress getSocket(String ctxId) {
        Element element = SESSION_MAP.get(ctxId);
        return element == null ? null : (InetSocketAddress) element.getObjectValue();
    }

    public static void remove(String ctxId) {
        SESSION_MAP.remove(ctxId);
    }
}
