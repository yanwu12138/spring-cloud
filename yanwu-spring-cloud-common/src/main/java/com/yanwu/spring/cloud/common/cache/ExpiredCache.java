package com.yanwu.spring.cloud.common.cache;

import com.yanwu.spring.cloud.common.pojo.ExpiredNode;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author XuBaofeng.
 * @date 2023/12/15 10:15.
 * <p>
 * description:
 */
@Slf4j
@Component
public class ExpiredCache<K, V> {

    /*** 使用ConcurrentHashMap缓存所有的数据 ***/
    private final Map<K, ExpiredNode<V>> expiredNodeCache = new ConcurrentHashMap<>();
    /*** 检测Key是否过期的定时任务 ***/
    private static final ScheduledExecutorService CHECK_EXPIRE_SCHEDULE = new ScheduledThreadPoolExecutor((1));

    @Value("${expired.cache.schedule.delay:5000}")
    private long delay;

    @PostConstruct
    public void checkExpiredSchedule() {
        CHECK_EXPIRE_SCHEDULE.scheduleWithFixedDelay(() -> {
            try {
                executorExpiredSchedule();
            } catch (Exception e) {
                log.error("check timeout schedule failed.", e);
            }
        }, delay, delay, TimeUnit.MILLISECONDS);
    }

    public ExpiredNode<V> put(@Nonnull K key, @Nonnull ExpiredNode<V> value) {
        value.resetTime();
        return expiredNodeCache.put(key, value);
    }

    public void putAll(@Nonnull Map<? extends K, ? extends ExpiredNode<V>> entries) {
        if (MapUtils.isEmpty(entries)) {
            return;
        }
        entries.values().forEach(ExpiredNode::resetTime);
        expiredNodeCache.putAll(entries);
    }

    public ExpiredNode<V> putIfAbsent(@Nonnull K key, @Nonnull ExpiredNode<V> value) {
        value.resetTime();
        return expiredNodeCache.putIfAbsent(key, value);
    }

    public ExpiredNode<V> get(@Nonnull K key) {
        ExpiredNode<V> value = expiredNodeCache.get(key);
        if (value != null) {
            value.resetTime();
            expiredNodeCache.put(key, value);
        }
        return value;
    }

    private void executorExpiredSchedule() {
        if (MapUtils.isEmpty(expiredNodeCache)) {
            return;
        }
        Set<Map.Entry<K, ExpiredNode<V>>> entries = expiredNodeCache.entrySet();
        if (CollectionUtils.isEmpty(entries)) {
            return;
        }
        log.debug("cache len: {}", JsonUtil.toString(expiredNodeCache.size()));
        long localtime = System.currentTimeMillis();
        Iterator<Map.Entry<K, ExpiredNode<V>>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<K, ExpiredNode<V>> entry = iterator.next();
            try {
                if (entry.getValue().timeout(localtime)) {
                    // ----- Key到达过期时间并且回调过期处理成功，删除Key
                    if (entry.getValue().callback()) {
                        iterator.remove();
                        log.debug("callback - timeout function key: {}", entry.getKey());
                    }
                }
            } catch (Exception e) {
                log.error("check  key: [{}] timeout failed.", entry.getKey(), e);
            }
        }
    }

    public static void main(String[] args) {
        ExpiredCache<String, String> cache = new ExpiredCache<>();
        cache.put("aaa", ExpiredNode.getInstance("aaa", 3_000L));
        ThreadUtil.sleep(1000);
        cache.put("bbb", ExpiredNode.getInstance("bbb", 7_000L));
        ThreadUtil.sleep(5000);
        cache.putIfAbsent("ccc", ExpiredNode.getInstance("ccc", 5_000L));
        ThreadUtil.sleep(2000);
        System.out.println(JsonUtil.toString(cache.get("ccc")));
        ThreadUtil.sleep(2000);
        Map<String, ExpiredNode<String>> temp = new ConcurrentHashMap<>();
        temp.put("ddd", ExpiredNode.getInstance("ddd", 5_000L));
        temp.put("eee", ExpiredNode.getInstance("eee", 5_000L));
        cache.putAll(temp);
    }

}
