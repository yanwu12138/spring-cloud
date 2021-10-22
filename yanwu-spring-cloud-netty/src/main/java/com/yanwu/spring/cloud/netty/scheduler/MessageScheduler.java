package com.yanwu.spring.cloud.netty.scheduler;

import com.yanwu.spring.cloud.netty.cache.ClientSessionCache;
import com.yanwu.spring.cloud.netty.cache.MessageCache;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

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
    @Resource
    private ClientSessionCache clientSessionCache;

    /**
     * 查看当前设备连接数量
     */
    @Scheduled(fixedRate = 3_000)
    public void senderMessage() {
        Set<String> sns = clientSessionCache.getOnlines();
        if (CollectionUtils.isEmpty(sns)) {
            return;
        }
        sns.forEach(sn -> messageCache.senderMessage(sn));
    }

}
