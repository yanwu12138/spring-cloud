package com.yanwu.spring.cloud.common.mvc.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author XuBaofeng.
 * @date 2018-11-15 18:50.
 * <p>
 * description:
 */
@Data
@NoArgsConstructor
public class BackVO<T> {

    private T data;

    private Boolean status;

    private Integer code;

    private String message;

    private String traceId;


}
