package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/9 16:10.
 * <p>
 * description:
 */
@Slf4j
@Component
public class RedisLockUtil<K extends Serializable, V extends Serializable> {
    /*** 多节点下使用redis分布式锁避免安全问题 ***/
    private static final String REDIS_LOCK = "redis_lock_";
    /*** 分布式锁过期时间 ***/
    private static final Integer EXPIRE_TIME = 30;
    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ValueOperations<String, V> redisLockOperation;

    /**
     * 获取锁
     *
     * @param key
     * @param value
     */
    public void tryLock(K key, V value) throws RuntimeException {
        int maxSize = 10;
        // ----- 尝试获取锁，当获取到锁，则直接返回，否则，循环尝试获取
        while (!redisLockOperation.setIfAbsent(REDIS_LOCK + key, value, EXPIRE_TIME, TimeUnit.SECONDS)) {
            // ----- 最多循环10次，当尝试了10次都没有获取到锁，则抛出异常
            if (maxSize == 0) {
                log.error("redis try lock fail. key: {}, value: {}", key, value);
                throw new RuntimeException("redis try lock fail.");
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (Exception e) {
                log.error("history try lock error.", e);
            }
            maxSize--;
        }
    }

    /**
     * 释放锁
     *
     * @param key
     */
    public void unLock(Long key) {
        redisLockOperation.getOperations().delete(REDIS_LOCK + key);
    }
}
