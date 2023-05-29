package com.yanwu.spring.cloud.common.pojo;


import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Nonnull;
import java.io.Serializable;
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
public class ExpiredHashMap<K, V> extends ConcurrentHashMap<K, ExpiredHashMap.ExpiredNode<V>> implements Serializable {
    private static final long serialVersionUID = -7451049176327506965L;

    /*** 该Map中Key的过期时间，单位：毫秒 ***/
    private static final AtomicLong EXPIRE_TIME = new AtomicLong();
    /*** 检测Key是否过期的定时任务 ***/
    private static final ScheduledExecutorService CHECK_EXPIRE_SCHEDULE = new ScheduledThreadPoolExecutor((1));

    /**
     * 构造该Map:
     * 该Map每1秒执行一次过期检测，当检测到Map中的Key过期时，会回调对应的function
     *
     * @param expire   Key的过期时间，单位：毫秒
     * @param function Key过期时的回调函数，并通过回调的结果判断过期Key处理是否成功
     */
    public ExpiredHashMap(@Nonnull Long expire, @Nonnull Function<Object, Boolean> function) {
        this(1_000L, expire, function);
    }

    /**
     * 构造该Map:
     * 该Map每1秒执行一次过期检测，当检测到Map中的Key过期时，会回调对应的function
     *
     * @param period   过期检测任务执行间隔时间，单位：毫秒
     * @param expire   Key的过期时间，单位：毫秒
     * @param function Key过期时的回调函数，并通过回调的结果判断过期Key处理是否成功
     */
    public ExpiredHashMap(@Nonnull Long period, @Nonnull Long expire, @Nonnull Function<Object, Boolean> function) {
        super();
        EXPIRE_TIME.set(expire);
        CHECK_EXPIRE_SCHEDULE.scheduleWithFixedDelay(() -> checkExpiredSchedule(function), (0), period, TimeUnit.MILLISECONDS);
    }

    /**
     * 执行检测任务
     *
     * @param function 回调函数
     */
    private void checkExpiredSchedule(@Nonnull Function<Object, Boolean> function) {
        try {
            Set<Entry<K, ExpiredNode<V>>> entries = this.entrySet();
            if (CollectionUtils.isEmpty(entries)) {
                return;
            }
            log.info("cache: {}", JsonUtil.toString(this));
            long localtime = System.currentTimeMillis();
            for (Entry<K, ExpiredNode<V>> entry : entries) {
                try {
                    if (entry.getValue().timeout(localtime, EXPIRE_TIME.get()) && function.apply(entry.getKey())) {
                        // ----- Key到达过期时间并且回调过期处理成功，删除Key
                        remove(entry.getKey());
                        log.info("callback - timeout function key: {}", entry.getKey());
                    }
                } catch (Exception e) {
                    log.error("check timeout error, key: {}.", entry.getKey(), e);
                }
            }
        } catch (Exception e) {
            log.error("check timeout error.", e);
        }
    }

    /*** 新增K-V时：给每个Key新增超时时间戳 ***/
    @Override
    public ExpiredNode<V> put(@Nonnull K key, @Nonnull ExpiredNode<V> value) {
        value.resetTime();
        value = super.put(key, value);
        return value;
    }

    /*** 查询Key时：刷新该Key的超时时间戳 ***/
    @Override
    public ExpiredNode<V> get(@Nonnull Object key) {
        ExpiredNode<V> value = super.get(key);
        if (value != null) {
            value.resetTime();
        }
        return value;
    }

    @Accessors(chain = true)
    public static class ExpiredNode<V> implements Serializable {
        private static final long serialVersionUID = 1197572171199448469L;

        @Getter
        @Setter
        private V value;
        @Getter
        private Long lastTime = System.currentTimeMillis();

        private ExpiredNode() {
        }

        public static <V> ExpiredNode<V> getInstance(V value) {
            ExpiredNode<V> result = new ExpiredNode<>();
            return result.setValue(value);
        }

        public void resetTime() {
            this.lastTime = System.currentTimeMillis();
        }

        public boolean timeout(long localTime, long expired) {
            return localTime - lastTime >= expired;
        }
    }

    public static void main(String[] args) {
        ExpiredHashMap<String, String> map = new ExpiredHashMap<>(3_000L, (key) -> {
            log.info("function - timeout function key: {}", key);
            return Boolean.TRUE;
        });
        map.put("aaa", ExpiredNode.getInstance("aaa"));
        ThreadUtil.sleep(1000);
        map.put("bab", ExpiredNode.getInstance("bbb"));
        ThreadUtil.sleep(5000);
        map.put("ccc", ExpiredNode.getInstance("ccc"));
        ThreadUtil.sleep(2000);
        System.out.println(JsonUtil.toString(map.getOrDefault("ccc", ExpiredNode.getInstance("123123"))));
    }

}
