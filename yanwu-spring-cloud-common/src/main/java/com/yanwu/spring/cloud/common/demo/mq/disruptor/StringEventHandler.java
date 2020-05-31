package com.yanwu.spring.cloud.common.demo.mq.disruptor;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-30 22:37:40.
 * <p>
 * describe: 得到事件的处理行为逻辑
 */
@Slf4j
public class StringEventHandler implements EventHandler<StringEvent> {
    public static Long count = 0L;

    @Override
    public void onEvent(StringEvent event, long sequence, boolean endOfBatch) throws Exception {
        log.info("thread: {}, event: {}, sequence: {}, endOfBatch: {}, count: {}",
                Thread.currentThread().getName(), event, sequence, endOfBatch, ++count);
    }
}
