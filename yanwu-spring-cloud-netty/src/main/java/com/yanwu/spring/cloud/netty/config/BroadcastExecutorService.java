package com.yanwu.spring.cloud.netty.config;

import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.netty.enums.BroadcastEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Baofeng Xu
 * @date 2021/4/25 16:36.
 * <p>
 * description: 一个type对应一个线程池
 */
@Slf4j
@Component
public class BroadcastExecutorService {
    private final ConcurrentHashMap<String, BroadcastExecutor> executorsMap = new ConcurrentHashMap<>();

    /**
     * 添加任务
     */
    public synchronized <T> Result<T> addRunnable(BroadcastEnum type, String magic, Callable<Result<T>> callable) {
        String key = getKey(type, magic);
        BroadcastExecutor broadcastExecutor = executorsMap.get(key);
        try {
            if (broadcastExecutor != null) {
                return broadcastExecutor.submit(callable).get();
            } else {
                BroadcastExecutor executor = BroadcastExecutor.newInstance(key);
                executorsMap.put(key, executor);
                return executor.submit(callable).get();
            }
        } catch (Exception e) {
            log.error("broadcast executor add runnable error.", e);
            return Result.failed();
        }
    }

    /**
     * 判断是否有线程正在运行
     */
    public boolean isActive(BroadcastEnum type, String id) {
        BroadcastExecutor executor = executorsMap.get(getKey(type, id));
        return executor != null && executor.getActiveCount() > 0;
    }

    private String getKey(BroadcastEnum type, String magic) {
        return type.getType() + "-" + magic;
    }
}
