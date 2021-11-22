package com.yanwu.spring.cloud.common.rocket.consumer;

import com.yanwu.spring.cloud.common.rocket.constant.ConsumerTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 10:50.
 * <p>
 * description:
 */
@Data
public class RocketConsumerConf {

    private String group;

    private String topic;

    /**
     * @see ConsumerTypeEnum
     */
    private int consumerType;

    private List<RocketConsumerListener> listeners;
}
