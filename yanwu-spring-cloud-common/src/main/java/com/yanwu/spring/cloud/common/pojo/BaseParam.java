package com.yanwu.spring.cloud.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2018-11-16 12:01.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class BaseParam<T> implements Serializable {
    private static final long serialVersionUID = -387045683597800606L;

    private T data;

}
