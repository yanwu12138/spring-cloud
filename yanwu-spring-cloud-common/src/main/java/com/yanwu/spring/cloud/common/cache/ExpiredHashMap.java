package com.yanwu.spring.cloud.common.cache;


import com.yanwu.spring.cloud.common.pojo.ExpiredNode;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
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
import java.util.function.Function;

/**
 * @author XuBaofeng.
 * @date 2023/5/24 11:04.
 * <p>
 * description: 带过期策略的HashMap
 * *****
 * 注意：该Map每个对象都会带有一个单独的ScheduledExecutorService，所以在使用该Map时需要注意使用那个场景，不能作为局部变量使用
 * *****
 */
@Slf4j
@SuppressWarnings("unused")
public class ExpiredHashMap<K, V> extends ConcurrentHashMap<K, ExpiredNode<V>> implements Serializable {
    private static final long serialVersionUID = -7451049176327506965L;

    /*** 检测Key是否过期的定时任务 ***/
    private static final ScheduledExecutorService CHECK_EXPIRE_SCHEDULE = new ScheduledThreadPoolExecutor((1));

    /**
     * 构造该Map:
     * 该Map每10秒执行一次过期检测，当检测到Map中的Key过期时，直接删除，不执行回调操作
     */
    public ExpiredHashMap() {
        this(10_000L);
    }

    /**
     * 构造该Map:
     * 该Map指定时间到达会执行一次过期检测，当检测到Map中的Key过期时，直接删除，不执行回调操作
     *
     * @param period 过期检测任务执行间隔时间，单位：毫秒
     */
    public ExpiredHashMap(@Nonnull Long period) {
        this(period, (val) -> Boolean.TRUE);
    }

    /**
     * 构造该Map:
     * 该Map每10秒执行一次过期检测，当检测到Map中的Key过期时，会回调对应的function
     *
     * @param function Key过期时的回调函数，并通过回调的结果判断过期Key处理是否成功
     */
    public ExpiredHashMap(@Nonnull Function<ExpiredNode<V>, Boolean> function) {
        this(10_000L, function);
    }

    /**
     * 构造该Map:
     * 该Map指定时间到达会执行一次过期检测，当检测到Map中的Key过期时，会回调对应的function
     *
     * @param period   过期检测任务执行间隔时间，单位：毫秒x
     * @param function Key过期时的回调函数，并通过回调的结果判断过期Key处理是否成功
     */
    public ExpiredHashMap(@Nonnull Long period, @Nonnull Function<ExpiredNode<V>, Boolean> function) {
        super();
        CHECK_EXPIRE_SCHEDULE.scheduleWithFixedDelay(() -> {
            try {
                checkExpiredSchedule(function);
            } catch (Exception e) {
                log.error("check timeout schedule failed.", e);
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    /**
     * 执行检测任务
     *
     * @param function 回调函数
     */
    private void checkExpiredSchedule(@Nonnull Function<ExpiredNode<V>, Boolean> function) {
        Set<Entry<K, ExpiredNode<V>>> entries = this.entrySet();
        if (CollectionUtils.isEmpty(entries)) {
            return;
        }
        log.debug("cache len: {}", JsonUtil.toString(this.size()));
        long localtime = System.currentTimeMillis();
        for (Entry<K, ExpiredNode<V>> entry : entries) {
            try {
                if (entry.getValue().timeout(localtime) && function.apply(entry.getValue())) {
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

}
