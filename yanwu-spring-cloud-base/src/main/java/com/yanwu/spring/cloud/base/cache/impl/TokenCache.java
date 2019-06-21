package com.yanwu.spring.cloud.base.cache.impl;

import com.yanwu.spring.cloud.base.cache.YanwuCacheManager;
import net.sf.ehcache.Element;
import org.springframework.stereotype.Component;

/**
 * @author XuBaofeng.
 * @date 2018-11-13 18:39.
 * <p>
 * description:
 */
@Component("tokenCache")
public class TokenCache implements YanwuCacheManager {

    @Override
    public void put(Long key, String value) {
        Element element = new Element(key, value);
        TOKEN_MAP.put(element);
    }

    @Override
    public String get(Long key) {
        Element element = TOKEN_MAP.get(key);
        return (String) element.getObjectValue();
    }

    @Override
    public Boolean remove(Long key) {
        return TOKEN_MAP.remove(key);
    }

}
