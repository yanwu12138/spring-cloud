package com.yanwu.spring.cloud.common.demo.mq.disruptor;

import lombok.Data;
import lombok.ToString;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-30 22:31:18.
 * <p>
 * describe:
 */
@Data
@ToString
public class StringEvent {

    private String value;

    public void set(String value) {
        this.value = value;
    }

}
