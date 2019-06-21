package com.yanwu.spring.cloud.common.data.res;

import lombok.Data;

/**
 * @author XuBaofeng.
 * @date 2018-11-07 15:19.
 * <p>
 * description:
 */
@Data
public class BaseVO<T> {

    private T data;

    private Boolean status;

    private Integer code;

    private String message;

    public BaseVO() {
    }

    public BaseVO(Boolean status) {
        this.status = status;
    }

    public BaseVO(Boolean status, T data) {
        this.data = data;
        this.status = status;
    }

    public BaseVO(Boolean status, Integer code, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public BaseVO(Boolean status, T data, Integer code, String message) {
        this.code = code;
        this.data = data;
        this.status = status;
        this.message = message;
    }
}
