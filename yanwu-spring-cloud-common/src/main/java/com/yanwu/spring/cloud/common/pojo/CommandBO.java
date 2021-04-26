package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/22 10:19.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class CommandBO<T> implements Serializable {
    private static final long serialVersionUID = -3876292807174570031L;

    private String ctxId;

    private T data;

}
