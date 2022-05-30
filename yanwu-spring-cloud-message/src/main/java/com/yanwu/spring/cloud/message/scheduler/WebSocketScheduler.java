package com.yanwu.spring.cloud.message.scheduler;

import com.yanwu.spring.cloud.message.service.WebSocketService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Baofeng Xu
 * @date 2022/5/30 12:13.
 * <p>
 * description:
 */
@Component
@EnableScheduling
public class WebSocketScheduler {

    @Scheduled(initialDelay = 300_000L, fixedDelay = 300_000L)
    public void closeTimeout() {
        WebSocketService.checkTimeout();
    }

}
