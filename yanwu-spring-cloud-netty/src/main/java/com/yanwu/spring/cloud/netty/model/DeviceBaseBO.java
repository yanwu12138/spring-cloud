package com.yanwu.spring.cloud.netty.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:28.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class DeviceBaseBO implements Serializable {
    private static final long serialVersionUID = 7222723385380290982L;

    /*** 消息ID ***/
    private String messageId;

}
