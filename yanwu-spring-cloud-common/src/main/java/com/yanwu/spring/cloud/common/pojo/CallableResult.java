package com.yanwu.spring.cloud.common.pojo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2021/4/22 16:23.
 * <p>
 * description:
 */
@ToString
@EqualsAndHashCode
public class CallableResult<T> implements Serializable {
    private static final long serialVersionUID = 8137315026981867597L;

    @Getter
    private T data;

    @Getter
    private Boolean status;

    @Getter
    private String message;

    public boolean isSuccess() {
        return status != null && status;
    }

    public static <T> CallableResult<T> success() {
        return success(null);
    }

    public static <T> CallableResult<T> success(T data) {
        return getInstance(data, Boolean.TRUE, null);
    }

    public static <T> CallableResult<T> failed() {
        return failed("执行失败");
    }

    public static <T> CallableResult<T> failed(String message) {
        return getInstance(null, Boolean.FALSE, message);
    }

    private static <T> CallableResult<T> getInstance(T data, Boolean status, String message) {
        return new CallableResult<T>().setData(data).setStatus(status).setMessage(message);
    }

    private CallableResult() {
    }

    private CallableResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    private CallableResult<T> setStatus(Boolean status) {
        this.status = status;
        return this;
    }

    private CallableResult<T> setMessage(String message) {
        this.message = message;
        return this;
    }
}
