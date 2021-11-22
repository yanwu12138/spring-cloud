package com.yanwu.spring.cloud.common.rocket;

import com.alibaba.rocketmq.common.message.MessageExt;
import com.aliyun.openservices.ons.api.Message;
import lombok.Data;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Baofeng Xu
 * @date 2021/11/22 10:57.
 * <p>
 * description:
 */
@Data
public class WrapperMessage {
    private static Charset UTF8 = StandardCharsets.UTF_8;

    private String topic;
    private String tag;
    private String key;
    private String body;

    public static WrapperMessage wrapper(Message message) {
        WrapperMessage wrapperMessage = new WrapperMessage();
        wrapperMessage.setTopic(message.getTopic());
        wrapperMessage.setKey(message.getKey());
        wrapperMessage.setTag(message.getTag());
        wrapperMessage.setBody(new String(message.getBody(), UTF8));
        return wrapperMessage;
    }

    public static WrapperMessage wrapper(MessageExt message) {
        WrapperMessage wrapperMessage = new WrapperMessage();
        wrapperMessage.setTopic(message.getTopic());
        wrapperMessage.setKey(message.getKeys());
        wrapperMessage.setTag(message.getTags());
        wrapperMessage.setBody(new String(message.getBody(), UTF8));
        return wrapperMessage;
    }

    public static WrapperMessage makeByTagAndBody(String tag, String body) {
        WrapperMessage wrapperMessage = new WrapperMessage();
        wrapperMessage.setTag(tag);
        wrapperMessage.setBody(body);
        return wrapperMessage;
    }

    public static WrapperMessage makeNoTopic(String tag, String key, String body) {
        WrapperMessage wrapperMessage = new WrapperMessage();
        wrapperMessage.setKey(key);
        wrapperMessage.setTag(tag);
        wrapperMessage.setBody(body);
        return wrapperMessage;
    }

    public byte[] getBodyAsByte() {
        return body.getBytes(UTF8);
    }

}
