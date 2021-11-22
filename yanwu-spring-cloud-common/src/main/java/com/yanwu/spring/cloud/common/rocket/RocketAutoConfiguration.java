package com.yanwu.spring.cloud.common.rocket;

import com.yanwu.spring.cloud.common.rocket.config.RocketProperties;
import com.yanwu.spring.cloud.common.rocket.consumer.RocketConsumerWrapperBean;
import com.yanwu.spring.cloud.common.rocket.consumer.WrapperMessageListener;
import com.yanwu.spring.cloud.common.rocket.producer.RocketProducerWrapperBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 11:25.
 * <p>
 * description: 根据配置文件配置初始化rockerMQ的consumer&&producer
 */
@Configuration
@ConditionalOnClass({WrapperMessageListener.class})
@ConditionalOnProperty(prefix = "spring.rocket", value = "enabled", havingValue = "true")
@EnableConfigurationProperties(RocketProperties.class)
public class RocketAutoConfiguration {

    @Resource
    private RocketProperties rocketProperties;
    @Resource
    private Map<String, WrapperMessageListener> listenerMap;

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public RocketConsumerWrapperBean rocketConsumerWrapperBean() {
        return new RocketConsumerWrapperBean(rocketProperties, listenerMap);
    }

    @Bean(initMethod = "init")
    @ConditionalOnMissingBean
    public RocketProducerWrapperBean rocketProducerWrapperBean() {
        return new RocketProducerWrapperBean(rocketProperties);
    }

}
