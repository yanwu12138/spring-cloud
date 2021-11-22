package com.yanwu.spring.cloud.common.rocket.producer;

import lombok.Data;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 11:52.
 * <p>
 * description:
 */
@Data
public class RocketProducerConf {

    private String group;

    private String topic;

}
