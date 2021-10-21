package com.yanwu.spring.cloud.netty.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2021/10/20 17:04.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class MessageQueueBO implements Serializable {
    private static final long serialVersionUID = -3798686682852024910L;

    /*** 消息 ***/
    private String message;


    /*** 如果需要去重：去重的规则 ***/
    private String code;

    private MessageQueueBO() {
    }

    /**
     * 生成消息队列缓存对象
     *
     * @param message 消息
     * @param code    消息类型[如果需要去重，则以code为去重规则]
     */
    public static MessageQueueBO getInstance(String message, String code) {
        return new MessageQueueBO().setMessage(message).setCode(code);
    }
}
