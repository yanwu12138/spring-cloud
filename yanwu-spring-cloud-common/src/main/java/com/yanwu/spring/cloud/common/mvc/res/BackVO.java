package com.yanwu.spring.cloud.common.mvc.res;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2018-11-15 18:50.
 * <p>
 * description:
 */
@Data
@NoArgsConstructor
public class BackVO<T> implements Serializable {
    private static final long serialVersionUID = -436487128792148712L;

    private T data;

    private Boolean status;

    private Integer code;

    private String message;

}
