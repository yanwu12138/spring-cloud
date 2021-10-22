package com.yanwu.spring.cloud.netty.model;

import lombok.Data;
import lombok.NonNull;
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
public class MessageQueueBO implements Comparable<MessageQueueBO>, Serializable {
    private static final long serialVersionUID = -3798686682852024910L;

    /*** 消息 ***/
    private String message;

    /*** 设备类型 ***/
    private String instance;

    /*** 消息的入队时间: 根据时间进行排序 ***/
    private Long time;

    private MessageQueueBO() {
    }

    /**
     * 生成消息队列缓存对象
     *
     * @param message  消息
     * @param instance 设备类型 [根据设备类型获取不同的处理server]
     */
    public static MessageQueueBO getInstance(String message, String instance) {
        return new MessageQueueBO().setMessage(message).setInstance(instance).setTime(System.currentTimeMillis());
    }

    @Override
    public int compareTo(@NonNull MessageQueueBO target) {
        return this.time.compareTo(target.getTime());
    }

}
