package com.yanwu.spring.cloud.common.mvc.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author XuBaofeng.
 * @date 2018-11-16 12:01.
 * <p>
 * description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseParam<T> {

    private T data;

    private String traceId;

}
