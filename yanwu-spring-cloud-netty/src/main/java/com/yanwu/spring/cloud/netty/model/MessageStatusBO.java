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
    /*** 消息的KEY，根据KEY进行去重 ***/
    private String messageKey;
    /*** 消息ID，最大值：Integer.MAX_VALUE，到达最大值时，将消息ID重置为1 ***/
    private Long messageId;
    /*** 最后一次发送消息的时间：间隔30秒才能发送下一次 ***/
    private Long messageLastSendTime;
    /*** 消息重传次数：每个消息是否可以发送 ***/
    private Integer tryTimes;

    public MessageStatusBO nextMessage(String key) {
        this.messageKey = key;
        this.messageId = this.messageId == Integer.MAX_VALUE ? 1 : this.messageId + 1;
        this.tryTimes = 3;
        this.messageLastSendTime = 0L;
        return this;
    }

    public MessageStatusBO clearMessage() {
        this.messageKey = null;
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
        if (messageKey != null) {
            return tryTimes > 0 && DeviceUtil.canSend(messageLastSendTime);
        }
        return false;
    }
}
