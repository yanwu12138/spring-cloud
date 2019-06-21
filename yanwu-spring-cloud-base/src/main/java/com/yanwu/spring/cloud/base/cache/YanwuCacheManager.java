package com.yanwu.spring.cloud.base.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * @author XuBaofeng.
 * @date 2018-11-13 17:58.
 * <p>
 * description:
 */
public interface YanwuCacheManager {

    Cache TOKEN_MAP = CacheManager.create().getCache("token");

    void put(Long key, String value);

    String get(Long key);

    Boolean remove(Long key);

}
