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
public class MessageStatusBO implements Serializable {
    private static final long serialVersionUID = -833769999652958242L;

    /*** 小站SN，唯一标识 ***/
    private String sn;
    /*** 消息的内容 ***/
    private MessageQueueBO message;
    /*** 消息ID，最大值：Integer.MAX_VALUE，到达最大值时，将消息ID重置为1 ***/
    private Long messageId;
    /*** 最后一次发送消息的时间：间隔30秒才能发送下一次 ***/
    private Long messageLastSendTime;
    /*** 消息重传次数：每个消息是否可以发送 ***/
    private Integer tryTimes;

    public static MessageStatusBO getInstance(String sn, MessageQueueBO message) {
        return new MessageStatusBO().setSn(sn).setMessage(message).setMessageId(0L)
                .setMessageLastSendTime(System.currentTimeMillis()).setTryTimes(3);
    }

    public MessageStatusBO clearMessage() {
        this.message = null;
        this.messageId = this.messageId == Integer.MAX_VALUE ? 1 : this.messageId + 1;
        this.tryTimes = 0;
        return this;
    }

    public MessageStatusBO successSend() {
        this.tryTimes--;
        this.messageLastSendTime = System.currentTimeMillis();
        return this;
    }

    public boolean canSend() {
        if (message != null) {
            return tryTimes > 0 && DeviceUtil.canSend(messageLastSendTime);
        }
        return false;
    }
}