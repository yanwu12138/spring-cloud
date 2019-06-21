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
@AllArgsConstructor
public class BackVO<T> {

    private T data;

    private Boolean status;

    private Integer code;

    private String message;

    private String traceId;

    public BackVO(T data) {
        this.data = data;
        this.status = Boolean.TRUE;
    }

    public BackVO(T data, String traceId) {
        this.data = data;
        this.traceId = traceId;
        this.status = Boolean.TRUE;
    }

    public BackVO(T data, String traceId, Boolean status) {
        this.data = data;
        this.traceId = traceId;
        this.status = status;
    }

    public BackVO(Integer code, String message) {
        this.status = Boolean.FALSE;
        this.code = code;
        this.message = message;
    }

    public BackVO(Integer code, String message, String traceId) {
        this.status = Boolean.FALSE;
        this.code = code;
        this.message = message;
        this.traceId = traceId;
    }
}
