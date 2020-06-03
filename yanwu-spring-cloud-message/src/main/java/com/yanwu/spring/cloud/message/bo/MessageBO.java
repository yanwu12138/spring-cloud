package com.yanwu.spring.cloud.message.bo;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
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

    @NotNull(
            message = "消息ID不能为NULL."
    )
    private String messageId;

    @NotNull(
            message = "消息体不能为NULL."
    )
    private T data;

    private Timestamp create;

}
