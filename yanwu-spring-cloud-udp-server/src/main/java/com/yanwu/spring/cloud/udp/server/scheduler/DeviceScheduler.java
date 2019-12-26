package com.yanwu.spring.cloud.udp.server.scheduler;

import com.yanwu.spring.cloud.udp.server.cache.ClientSessionMap;
import com.yanwu.spring.cloud.udp.server.swing.SwingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:30.
 * <p>
 * description:
 */
@Slf4j
@Component
public class DeviceScheduler {

    @Scheduled(fixedRate = 5000)
    public void sessionSync() {
        List sns = ClientSessionMap.keySet();
        SwingUtil.syncDeviceList(sns);
    }

}
