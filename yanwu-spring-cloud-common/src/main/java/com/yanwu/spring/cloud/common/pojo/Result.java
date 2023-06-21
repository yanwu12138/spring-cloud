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
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 8137315026981867597L;
    private static final String ERROR = "执行失败!";

    @Getter
    private T data;

    @Getter
    private Boolean status;

    @Getter
    private String message;

    public boolean isSuccess() {
        return status != null && status;
    }

    public boolean nonNull() {
        return isSuccess() && data != null;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return getInstance(data, Boolean.TRUE, null);
    }

    public static <T> Result<T> failed() {
        return failed(ERROR);
    }

    public static <T> Result<T> failed(T data) {
        return failed(data, ERROR);
    }

    public static <T> Result<T> failed(String message) {
        return failed(null, message);
    }

    public static <T> Result<T> failed(T data, String message) {
        return getInstance(data, Boolean.FALSE, message);
    }

    private static <T> Result<T> getInstance(T data, Boolean status, String message) {
        return new Result<T>().setData(data).setStatus(status).setMessage(message);
    }

    private Result() {
    }

    private Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    private Result<T> setStatus(Boolean status) {
        this.status = status;
        return this;
    }

    private Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

}
