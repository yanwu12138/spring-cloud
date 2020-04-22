package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/22 10:19.
 * <p>
 * description:
 */
@Data
public class CommandBO<T> implements Serializable {

    private String ctxId;

    private T data;

}
