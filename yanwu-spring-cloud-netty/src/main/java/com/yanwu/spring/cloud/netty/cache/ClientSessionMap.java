package com.yanwu.spring.cloud.netty.cache;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

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
            int size = 0;
            List keys = SESSION_MAP.getKeys();
            if (CollectionUtils.isNotEmpty(keys)) {
                size = keys.size();
            }
            log.info("当前检测到长连接数目: {}", size);
        } catch (Exception e) {
            log.error("[local cache]monitor has occured error,cause:" + e);
        }
    }

    public static void put(String ctxId, ChannelHandlerContext channel) {
        Element ctxElement = new Element(ctxId, channel);
        SESSION_MAP.put(ctxElement);
    }

    public static ChannelHandlerContext get(String ctxId) {
        Element element = SESSION_MAP.get(ctxId);
        return element == null ? null : (ChannelHandlerContext) element.getObjectValue();
    }

    public static Boolean remove(String ctxId) {
        return SESSION_MAP.remove(ctxId);
    }
}
