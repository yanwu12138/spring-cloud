package com.yanwu.spring.cloud.netty.model;

import com.yanwu.spring.cloud.netty.util.DeviceUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2021/10/20 16:57.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class MessageStatusBO<T> implements Serializable {
    private static final long serialVersionUID = -833769999652958242L;

    /*** 小站SN，唯一标识 ***/
    private String sn;
    /*** 消息的内容 ***/
    private MessageQueueBO<T> message;
    /*** 消息开始发送的时间 ***/
    private Long time;
    /*** 消息ID，最大值：Integer.MAX_VALUE，到达最大值时，将消息ID重置为1 ***/
    private Long messageId;
    /*** 最后一次发送消息的时间：间隔30秒才能发送下一次 ***/
    private Long lastSendTime;
    /*** 消息重传次数：每个消息是否可以发送 ***/
    private Integer tryNumber;

    public static <T> MessageStatusBO<T> getMessage(String sn, MessageQueueBO<T> message) {
        return nextMessage(sn, message, 1L);
    }

    public static <T> MessageStatusBO<T> nextMessage(String sn, MessageQueueBO<T> message, Long messageId) {
        MessageStatusBO<T> result = new MessageStatusBO<>();
        messageId = messageId >= Integer.MAX_VALUE ? 1 : messageId + 1;
        long time = System.currentTimeMillis();
        return result.setSn(sn).setMessage(message).setTime(time).setMessageId(messageId).setLastSendTime(time).setTryNumber(3);
    }

    public MessageStatusBO<T> successSend() {
        this.tryNumber--;
        this.lastSendTime = System.currentTimeMillis();
        return this;
    }

    public boolean canSend() {
        if (message != null) {
            return tryNumber > 0 && DeviceUtil.canSend(lastSendTime);
        }
        return false;
    }
}
