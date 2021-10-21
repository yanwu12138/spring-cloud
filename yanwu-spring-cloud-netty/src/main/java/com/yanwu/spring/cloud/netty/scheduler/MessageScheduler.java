package com.yanwu.spring.cloud.netty.scheduler;

import com.yanwu.spring.cloud.netty.cache.ClientSessionMap;
import com.yanwu.spring.cloud.netty.cache.MessageCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Baofeng Xu
 * @date 2021/10/21 10:15.
 * <p>
 * description:
 */
@Component
public class MessageScheduler {

    @Resource
    private MessageCache messageCache;

    /**
     * 查看当前设备连接数量
     */
    @Scheduled(fixedRate = 3_000)
    public void senderMessage() {
        ClientSessionMap.sessionSync();
    }

}
