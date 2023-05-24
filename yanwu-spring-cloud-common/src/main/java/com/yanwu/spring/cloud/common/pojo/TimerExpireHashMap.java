package com.yanwu.spring.cloud.common.pojo;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author XuBaofeng.
 * @date 2023/5/24 11:04.
 * <p>
 * description: 带过期策略的HashMap
 */
public class TimerExpireHashMap<K, V> extends ConcurrentHashMap<K, V> implements Serializable {
    private static final long serialVersionUID = -7451049176327506965L;
    private static final Map<Object, Long> EXPIRED_KEY_CACHE = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService SWAP_EXPIRED_POOL = new ScheduledThreadPoolExecutor(1);

    public TimerExpireHashMap(long period, TimeUnit unit) {
        super();
        SWAP_EXPIRED_POOL.scheduleAtFixedRate(() -> {
            System.out.println("1111111111");
        }, 0, period, unit);
    }

    public TimerExpireHashMap(long period, TimeUnit unit, Runnable function) {
        super();
        SWAP_EXPIRED_POOL.scheduleAtFixedRate(() -> {
            System.out.println("2222222222");
            function.run();
        }, 0, period, unit);
    }

    public V put(K key, V value, long expireTime) {
        value = put(key, value);
        EXPIRED_KEY_CACHE.put(key, expireTime);
        return value;
    }

    public static void main(String[] args) {
        new TimerExpireHashMap<String, String>(3000, TimeUnit.MILLISECONDS, () -> {
            System.out.println("3333333333");
        });
    }
}
