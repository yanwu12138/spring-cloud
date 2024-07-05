package com.yanwu.spring.cloud.netty.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author Baofeng Xu
 * @date 2021/4/25 16:37.
 * <p>
 * description:
 */
@Slf4j
public class BroadcastExecutor extends ThreadPoolTaskExecutor {
    private static final long serialVersionUID = -820900327456541827L;

    private String key;

    private BroadcastExecutor() {
    }

    protected static BroadcastExecutor newInstance(String key) {
        BroadcastExecutor executor = new BroadcastExecutor();
        executor.key = key;
        // ----- 每个线程池最大线程数为1
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        // ----- 每个线程池队列大小为0
        executor.setQueueCapacity(0);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(300);
        // ----- 配置线程池中的线程的名称前缀
        executor.setThreadNamePrefix("broadcast-thread-" + key + "-");
        executor.setThreadFactory(r -> {
            Thread thread = executor.createThread(r);
            thread.setUncaughtExceptionHandler((t1, e) -> log.error("broadcast throws exception: {}", t1, e));
            return thread;
        });
        executor.initialize();
        return executor;
    }

    @Override
    protected void finalize() {
        log.info("broadcast thread 线程池开始回收 key: {}", key);
        super.shutdown();
    }

}
