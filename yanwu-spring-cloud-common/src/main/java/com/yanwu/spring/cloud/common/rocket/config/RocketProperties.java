package com.yanwu.spring.cloud.common.rocket.config;

import com.yanwu.spring.cloud.common.rocket.consumer.RocketConsumerConf;
import com.yanwu.spring.cloud.common.rocket.producer.RocketProducerConf;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 10:55.
 * <p>
 * description:
 */
@Data
@ConfigurationProperties(prefix = "spring.rocket")
public class RocketProperties {

    private String nameServer;

    private boolean enable;

    private RocketConsumerConf[] consumer;

    private RocketProducerConf[] producer;

}
