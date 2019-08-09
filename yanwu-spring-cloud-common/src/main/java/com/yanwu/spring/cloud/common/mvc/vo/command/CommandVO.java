package com.yanwu.spring.cloud.common.mvc.vo.command;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 14:48.
 * <p>
 * description:
 */
@Data
public class CommandVO<T> implements Serializable {
    private static final long serialVersionUID = 7909874535444291299L;

    private String ctxId;

    private T data;

}
