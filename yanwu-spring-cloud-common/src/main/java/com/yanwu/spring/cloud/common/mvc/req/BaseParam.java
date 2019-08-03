package com.yanwu.spring.cloud.common.mvc.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2018-11-16 12:01.
 * <p>
 * description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseParam<T> implements Serializable {
    private static final long serialVersionUID = -387045683597800606L;

    private T data;

    private String traceId;

}
