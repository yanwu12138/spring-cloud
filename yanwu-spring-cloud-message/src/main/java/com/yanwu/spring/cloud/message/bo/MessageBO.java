package com.yanwu.spring.cloud.message.bo;

import com.yanwu.spring.cloud.common.utils.JsonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Timestamp;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/14 10:05.
 * <p>
 * description:
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class MessageBO<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -7386281146800585632L;

    @NotNull(
            message = "消息ID不能为NULL."
    )
    private String messageId;

    @NotNull(
            message = "消息体不能为NULL."
    )
    private T data;

    @NotNull(
            message = "消息类型不能为NULL."
    )
    private String type;

    private Timestamp create;

    public static MessageBO getInstance(byte[] message) {
        return JsonUtil.toObject(new String(message, Charset.defaultCharset()), MessageBO.class);
    }
}
