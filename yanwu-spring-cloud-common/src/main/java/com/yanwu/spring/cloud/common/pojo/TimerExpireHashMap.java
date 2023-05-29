package com.yanwu.spring.cloud.common.pojo;


import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

/**
 * @author XuBaofeng.
 * @date 2023/5/24 11:04.
 * <p>
 * description: 带过期策略的HashMap
 */
@Slf4j
public class TimerExpireHashMap<K, V> extends ConcurrentHashMap<K, V> implements Serializable {
    private static final long serialVersionUID = -7451049176327506965L;
    /*** 该Map中Key的过期时间，单位：毫秒 ***/
    private static final AtomicLong EXPIRED_TIME = new AtomicLong();
    /*** 该Map每个Key的最后一次访问时间：毫秒时间戳 ***/
    private static final Map<Object, AtomicLong> EXPIRED_KEY_CACHE = new ConcurrentHashMap<>();
    /*** 检测Key是否过期的定时任务 ***/
    private static final ScheduledExecutorService SWAP_EXPIRED_POOL = new ScheduledThreadPoolExecutor((1));

    /**
     * 构造该Map:
     * 该Map每1秒执行一次过期检测，当检测到Map中的Key过期时，会回调对应的function
     *
     * @param expired  Key的过期时间，单位：毫秒
     * @param function Key过期时的回调函数，并通过回调的结果判断过期Key处理是否成功
     */
    public TimerExpireHashMap(@Nonnull Long expired, @Nonnull Function<Object, Boolean> function) {
        this(1_000L, expired, function);
    }

    /**
     * 构造该Map:
     * 该Map每1秒执行一次过期检测，当检测到Map中的Key过期时，会回调对应的function
     *
     * @param period   过期检测任务执行间隔时间，单位：毫秒
     * @param expired  Key的过期时间，单位：毫秒
     * @param function Key过期时的回调函数，并通过回调的结果判断过期Key处理是否成功
     */
    public TimerExpireHashMap(@Nonnull Long period, @Nonnull Long expired, @Nonnull Function<Object, Boolean> function) {
        super();
        EXPIRED_TIME.set(expired);
        SWAP_EXPIRED_POOL.scheduleWithFixedDelay(() -> {
            try {
                Set<Entry<Object, AtomicLong>> entries = EXPIRED_KEY_CACHE.entrySet();
                if (CollectionUtils.isEmpty(entries)) {
                    return;
                }
                log.info("cache: {}", this);
                long localtime = System.currentTimeMillis();
                for (Entry<Object, AtomicLong> entry : entries) {
                    try {
                        if (localtime > entry.getValue().get()) {
                            if (this.containsKey(entry.getKey()) && function.apply(entry.getKey())) {
                                // ----- Key到达过期时间并且回调过期处理成功，删除Key
                                remove(entry.getKey());
                                log.info("callback - timeout function key: {}", entry.getKey());
                            } else {
                                remove(entry.getKey());
                            }
                        }
                    } catch (Exception e) {
                        log.error("check timeout error, key: {}.", entry.getKey(), e);
                    }
                }
            } catch (Exception e) {
                log.error("check timeout error.", e);
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    /*** 新增K-V时：给每个Key新增超时时间戳 ***/
    @Override
    public V put(@Nonnull K key, @Nonnull V value) {
        value = super.put(key, value);
        setExpireTime(key);
        return value;
    }

    /*** 查询Key时：刷新该Key的超时时间戳 ***/
    @Override
    public V get(@Nonnull Object key) {
        V value = super.get(key);
        if (value != null) {
            setExpireTime(key);
        }
        return value;
    }

    /*** 删除Key时：也删除该Key的时间戳缓存 ***/
    @Override
    public V remove(@Nonnull Object key) {
        V value = super.remove(key);
        EXPIRED_KEY_CACHE.remove(key);
        return value;
    }

    /*** 刷新Key的超时时间戳缓存 ***/
    private synchronized void setExpireTime(Object key) {
        long expireTime = System.currentTimeMillis() + EXPIRED_TIME.get();
        EXPIRED_KEY_CACHE.put(key, new AtomicLong(expireTime));
    }

    public static void main(String[] args) {
        TimerExpireHashMap<String, String> map = new TimerExpireHashMap<>(5000L, (key) -> {
            log.info("function - timeout function key: {}", key);
            return Boolean.TRUE;
        });
        map.put("aaa", "aaa");
        ThreadUtil.sleep(1000);
        map.put("bab", "bbb");
        ThreadUtil.sleep(5000);
        map.put("ccc", "ccc");
        ThreadUtil.sleep(2000);
        System.out.println(map.get("ccc"));
    }

}
