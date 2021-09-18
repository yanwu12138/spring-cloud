package com.yanwu.spring.cloud.netty;

import com.yanwu.spring.cloud.common.runner.InitSqlRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:36.
 * <p>
 * description:
 * * @EnableEurekaClient        启动eureka注册
 * * @EnableDiscoveryClient     启动ribbon负载均衡
 * * @EnableFeignClients        启动feign服务发现功能
 * * @EnableHystrix             启动断路器
 * * @EnableScheduling          启用scheduler定时任务
 */
@EnableHystrix
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.yanwu.spring.cloud"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {InitSqlRunner.class})})
public class YanwuSpringCloudNettyApplication {

    public static void main(String[] args) {
        SpringApplication.run(YanwuSpringCloudNettyApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Executor nettyExecutor() {
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
        executor.setThreadNamePrefix("netty-");
        // ----- 设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // ----- 执行初始化
        executor.initialize();
        return executor;
    }
}