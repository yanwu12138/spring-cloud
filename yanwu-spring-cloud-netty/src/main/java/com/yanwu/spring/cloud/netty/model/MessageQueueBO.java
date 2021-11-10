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
public class MessageQueueBO<T> implements Comparable<MessageQueueBO<T>>, Serializable {
    private static final long serialVersionUID = -3798686682852024910L;

    /*** 消息的唯一标识 ***/
    private String key;

    /*** 消息 ***/
    private T message;

    /*** 设备类型: 对应不通的handler执行消息发送 ***/
    private Class<?> instance;

    /*** 消息的入队时间: 根据时间进行排序 ***/
    private Long time;

    /**
     * 生成消息队列缓存对象
     *
     * @param message  消息
     * @param instance 设备类型 [根据设备类型获取不同的处理server]
     */
    public static <T> MessageQueueBO<T> getInstance(T message, Class<?> instance) {
        MessageQueueBO<T> result = new MessageQueueBO<>();
        result.setMessage(message).setInstance(instance).setTime(System.currentTimeMillis());
        return result;
    }

    @Override
    public int compareTo(@NonNull MessageQueueBO<T> target) {
        return this.time.compareTo(target.getTime());
    }
}
