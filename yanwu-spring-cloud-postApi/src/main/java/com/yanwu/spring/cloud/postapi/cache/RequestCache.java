package com.yanwu.spring.cloud.postapi.cache;

import com.yanwu.spring.cloud.postapi.bo.RequestCacheBO;
import com.yanwu.spring.cloud.postapi.bo.RequestInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author XuBaofeng.
 * @date 2024/5/17 15:53.
 * <p>
 * description:
 */
public class RequestCache {

    private static final Map<String, RequestInfo<?>> INSTANCE = new ConcurrentHashMap<>();

    public static void createCache(String path, RequestCacheBO request) {
        if (request.getRequestInfo() == null) {
            return;
        }
        INSTANCE.put(path, request.getRequestInfo());
    }

    public static RequestInfo<?> findRequestInfo(String path) {
        if (!INSTANCE.containsKey(path)) {
            return null;
        }
        return INSTANCE.get(path);
    }
}
