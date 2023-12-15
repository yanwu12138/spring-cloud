package com.yanwu.spring.cloud.common.cache;


import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

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
public class ExpiredMap<K, V> extends ConcurrentHashMap<K, ExpiredMap.ExpiredNode<V>> implements Serializable {
    private static final long serialVersionUID = -7451049176327506965L;

    /*** 该Map中Key的过期时间，单位：毫秒 ***/
    private static final AtomicLong EXPIRE_TIME = new AtomicLong();
    /*** 检测Key是否过期的定时任务 ***/
    private static final ScheduledExecutorService CHECK_EXPIRE_SCHEDULE = new ScheduledThreadPoolExecutor((1));

    /***
     * 构造该Map:
     * 该Map每1秒执行一次过期检测，当检测到Map中的Key过期时，会回调对应的function
     * @param expire   Key的过期时间，单位：毫秒
     * @param function Key过期时的回调函数，并通过回调的结果判断过期Key处理是否成功
     */
    public ExpiredMap(@Nonnull Long expire, @Nonnull Function<ExpiredNode<V>, Boolean> function) {
        this(1_000L, expire, function);
    }

    /***
     * 构造该Map:
     * 该Map指定时间到达会执行一次过期检测，当检测到Map中的Key过期时，会回调对应的function
     * @param period   过期检测任务执行间隔时间，单位：毫秒
     * @param expire   Key的过期时间，单位：毫秒
     * @param function Key过期时的回调函数，并通过回调的结果判断过期Key处理是否成功
     */
    public ExpiredMap(@Nonnull Long period, @Nonnull Long expire, @Nonnull Function<ExpiredNode<V>, Boolean> function) {
        super();
        EXPIRE_TIME.set(expire);
        CHECK_EXPIRE_SCHEDULE.scheduleWithFixedDelay(() -> {
            try {
                checkExpiredSchedule(function);
            } catch (Exception e) {
                log.error("check timeout schedule failed.", e);
            }
        }, 0, period, TimeUnit.MILLISECONDS);
    }

    /***
     * 执行检测任务
     * @param function 回调函数
     */
    private void checkExpiredSchedule(@Nonnull Function<ExpiredNode<V>, Boolean> function) {
        Set<Entry<K, ExpiredNode<V>>> entries = this.entrySet();
        if (CollectionUtils.isEmpty(entries)) {
            return;
        }
        log.debug("cache: {}", JsonUtil.toString(this));
        long localtime = System.currentTimeMillis();
        for (Entry<K, ExpiredNode<V>> entry : entries) {
            try {
                if (entry.getValue().timeout(localtime, EXPIRE_TIME.get()) && function.apply(entry.getValue())) {
                    // ----- Key到达过期时间并且回调过期处理成功，删除Key
                    remove(entry.getKey());
                    log.debug("callback - timeout function key: {}", entry.getKey());
                }
            } catch (Exception e) {
                log.error("check  key: [{}] timeout failed.", entry.getKey(), e);
            }
        }
    }

    /*** 新增K-V时：给每个Key新增超时时间戳 ***/
    @Override
    public ExpiredNode<V> put(@Nonnull K key, @Nonnull ExpiredNode<V> value) {
        value.resetTime();
        return super.put(key, value);
    }

    /*** 批量插入K-V时：给每个Key新增超时时间戳 ***/
    @Override
    public void putAll(@Nonnull Map<? extends K, ? extends ExpiredNode<V>> entries) {
        if (MapUtils.isEmpty(entries)) {
            return;
        }
        entries.values().forEach(ExpiredNode::resetTime);
        super.putAll(entries);
    }

    /*** 新增K-V时：给每个Key新增超时时间戳 ***/
    @Override
    public ExpiredNode<V> putIfAbsent(@Nonnull K key, @Nonnull ExpiredNode<V> value) {
        value.resetTime();
        return super.putIfAbsent(key, value);
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

    @ToString
    @Accessors(chain = true)
    public static class ExpiredNode<V> implements Serializable {
        private static final long serialVersionUID = 1197572171199448469L;

        @Getter
        @Setter
        private V value;

        private Long lastTime = System.currentTimeMillis();

        private ExpiredNode() {
        }

        public static <V> ExpiredNode<V> getInstance(V value) {
            ExpiredNode<V> result = new ExpiredNode<>();
            return result.setValue(value);
        }

        /*** 重置最后访问时间 ***/
        public void resetTime() {
            this.lastTime = System.currentTimeMillis();
        }

        /*** 检查是否超时：【true: 超时; false: 未超时】 ***/
        public boolean timeout(long localTime, long expired) {
            return localTime - lastTime >= expired;
        }
    }

    public static void main(String[] args) {
        ExpiredMap<String, String> map = new ExpiredMap<>(3_000L, (val) -> {
            log.info("function - timeout function key: {}", val);
            return Boolean.TRUE;
        });
        map.put("aaa", ExpiredNode.getInstance("aaa"));
        ThreadUtil.sleep(1000);
        map.put("bbb", ExpiredNode.getInstance("bbb"));
        ThreadUtil.sleep(5000);
        map.put("ccc", ExpiredNode.getInstance("ccc"));
        ThreadUtil.sleep(2000);
        System.out.println(JsonUtil.toString(map.getOrDefault("ccc", ExpiredNode.getInstance("123123"))));
        ThreadUtil.sleep(2000);
        Map<String, ExpiredNode<String>> temp = new ConcurrentHashMap<>();
        temp.put("ddd", ExpiredNode.getInstance("ddd"));
        temp.put("eee", ExpiredNode.getInstance("eee"));
        map.putAll(temp);
    }

}
