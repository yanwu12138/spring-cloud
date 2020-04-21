package com.yanwu.spring.cloud.netty.scheduler;

import com.yanwu.spring.cloud.netty.cache.ClientSessionMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:30.
 * <p>
 * description:
 */
@Component
public class SessionMapScheduler {

    @Scheduled(fixedRate = 1000 * 30)
    public void sessionSync() {
        ClientSessionMap.sessionSync();
    }

}
