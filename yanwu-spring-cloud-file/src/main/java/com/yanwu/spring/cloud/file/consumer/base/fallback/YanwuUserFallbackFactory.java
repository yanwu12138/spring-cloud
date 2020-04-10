package com.yanwu.spring.cloud.file.consumer.base.fallback;

import com.yanwu.spring.cloud.file.consumer.base.YanwuUserConsumer;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020/04/03
 * <p>
 * describe:
 */
@Slf4j
@Component
public class YanwuUserFallbackFactory implements FallbackFactory<YanwuUserConsumer> {

    private final YanwuUserConsumer consumer;

    public YanwuUserFallbackFactory(YanwuUserConsumer consumer) {
        this.consumer = consumer;
    }


    @Override
    public YanwuUserConsumer create(Throwable cause) {
        log.error("yanwu user consumer error.", cause);
        return consumer;
    }

}
