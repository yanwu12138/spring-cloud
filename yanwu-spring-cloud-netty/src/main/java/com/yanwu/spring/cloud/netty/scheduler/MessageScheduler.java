package com.yanwu.spring.cloud.netty.scheduler;

import com.yanwu.spring.cloud.common.pojo.CallableResult;
import com.yanwu.spring.cloud.common.utils.DateUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.netty.cache.ClientSessionCache;
import com.yanwu.spring.cloud.netty.cache.MessageCache;
import com.yanwu.spring.cloud.netty.model.MessageQueueBO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * @author Baofeng Xu
 * @date 2021/10/21 10:15.
 * <p>
 * description:
 */
@Slf4j
@Component
public class MessageScheduler<T> {

    @Resource
    private MessageCache<T> messageCache;
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

    /***
     * 每天凌晨4点中删除一个月前的消息
     */
    @Scheduled(cron = "0 0 4 * * ? *")
    public void removeExpiredMessage() {
        try {
            CallableResult<Map<String, MessageQueueBO<T>>> result = messageCache.removeExpiredMessage();
            if (!result.getStatus()) {
                log.error("remove expired message failed. result: {}", result);
                return;
            }
            // ----- 将过期消息写道本地文件
            Map<String, MessageQueueBO<T>> messages = result.getData();
            if (MapUtils.isEmpty(messages)) {
                log.info("remove expired message success. result: {}", result);
                return;
            }
            LocalDateTime dateTime = LocalDateTime.now();
            String filepath = "/Users/xubaofeng/yanwu/file" + File.separator + dateTime.getYear()
                    + File.separator + DateUtil.filling(dateTime.getMonthValue()) + File.separator + DateUtil.datetimeStr(dateTime);
            messages.forEach((key, message) -> {
                try {
                    byte[] block = (key + " - " + JsonUtil.toCompactJsonString(message) + "\r\n").getBytes(StandardCharsets.UTF_8);
                    FileUtil.appendWrite(filepath, block);
                } catch (Exception e) {
                    log.error("write expired message error.", e);
                }
            });
        } catch (Exception e) {
            log.error("remove expired message error.", e);
        }
    }

}
