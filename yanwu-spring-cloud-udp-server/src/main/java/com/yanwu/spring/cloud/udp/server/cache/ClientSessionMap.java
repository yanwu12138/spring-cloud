package com.yanwu.spring.cloud.udp.server.cache;

import com.yanwu.spring.cloud.udp.server.model.DeviceChannel;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

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

    public static void put(String sn, DeviceChannel deviceChannel) {
        Element element = SESSION_MAP.get(sn);
        if (element == null) {
            element = new Element(sn, deviceChannel);
        }
        SESSION_MAP.put(element);
    }

    public static DeviceChannel get(String ctxId) {
        Element element = SESSION_MAP.get(ctxId);
        return element == null ? null : (DeviceChannel) element.getObjectValue();
    }

    public static List keySet() {
        SESSION_MAP.getKeysWithExpiryCheck();
        return SESSION_MAP.getKeys();
    }

}
