package com.yanwu.spring.cloud.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 * @author Baofeng Xu
 * @date 2021/7/16 15:01.
 * <p>
 * description:
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "yanwu.spring.cloud.scheduler.enabled", value = "enable", havingValue = "true", matchIfMissing = true)
public class SchedulerConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadFactory scheduleThreadFactory = new CustomizableThreadFactory("schedule-");
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(5, scheduleThreadFactory);
        taskRegistrar.setScheduler(scheduler);
    }

}
