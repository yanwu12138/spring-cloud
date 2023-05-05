package com.yanwu.spring.cloud.common.config;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3 15:37.
 * <p>
 * description:
 */
@Configuration
public class BeanConfig {


    @Bean
    public Mapper getMapper() {
        return new DozerBeanMapper();
    }

    /**
     * 通用线程池
     *
     * @return 线程池
     */
    @Bean
    public Executor commonsExecutors() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // ----- 设置核心线程数
        executor.setCorePoolSize(50);
        // ----- 设置最大线程数
        executor.setMaxPoolSize(100);
        // ----- 设置队列容量
        executor.setQueueCapacity(Integer.MAX_VALUE);
        // ----- 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(120);
        // ----- 设置默认线程名称
        executor.setThreadNamePrefix("commons-executor-");
        // ----- 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // ----- 执行初始化
        executor.initialize();
        return executor;
    }

    /**
     * 系统初始化线程池
     */
    @Bean
    public Executor initExecutors() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // ----- 设置核心线程数
        executor.setCorePoolSize(1);
        // ----- 设置最大线程数
        executor.setMaxPoolSize(10);
        // ----- 设置队列容量
        executor.setQueueCapacity(20);
        // ----- 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(120);
        // ----- 设置默认线程名称
        executor.setThreadNamePrefix("main-init-executor-");
        // ----- 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // ----- 执行初始化
        executor.initialize();
        return executor;
    }

}
