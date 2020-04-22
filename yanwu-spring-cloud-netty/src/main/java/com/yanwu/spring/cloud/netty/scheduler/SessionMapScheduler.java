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

    /**
     * 查看当前设备连接数量
     */
    @Scheduled(fixedRate = 1000 * 60 * 5)
    public void sessionSync() {
        ClientSessionMap.sessionSync();
    }

}
