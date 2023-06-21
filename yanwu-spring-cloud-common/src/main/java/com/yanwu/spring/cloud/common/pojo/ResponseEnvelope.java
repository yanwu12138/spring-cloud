package com.yanwu.spring.cloud.common.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author Administrator
 * descreption: controller响应实体
 */
@ToString
@EqualsAndHashCode
@JsonInclude(Include.NON_NULL)
public class ResponseEnvelope<T> implements Serializable {
    private static final long serialVersionUID = 5713406382349859603L;

    /*** 数据 ***/
    @Getter
    private T data;
    /*** 接口返回状态 ***/
    @Getter
    private Boolean status;
    /*** 接口错误码 ***/
    @Getter
    private Integer code;
    /*** 接口错误提示语 ***/
    @Getter
    private String message;

    public static <T> ResponseEnvelope<T> success() {
        return success(null);
    }

    public static <T> ResponseEnvelope<T> success(T data) {
        return getInstance(data, Boolean.TRUE, HttpStatus.OK, null);
    }

    public static <T> ResponseEnvelope<T> failed() {
        return failed(HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    public static <T> ResponseEnvelope<T> failed(HttpStatus status) {
        return failed(status, null);
    }

    public static <T> ResponseEnvelope<T> failed(String message) {
        return failed(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static <T> ResponseEnvelope<T> failed(HttpStatus status, String message) {
        status = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        message = StringUtils.isNotBlank(message) ? message : "服务器内部错误";
        return getInstance(null, Boolean.FALSE, status, message);
    }

    private static <T> ResponseEnvelope<T> getInstance(T data, Boolean status, HttpStatus code, String message) {
        return new ResponseEnvelope<T>().setData(data).setStatus(status).setCode(code.value()).setMessage(message);
    }

    private ResponseEnvelope() {
    }

    private ResponseEnvelope<T> setData(T data) {
        this.data = data;
        return this;
    }

    private ResponseEnvelope<T> setStatus(Boolean status) {
        this.status = status;
        return this;
    }

    private ResponseEnvelope<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    private ResponseEnvelope<T> setMessage(String message) {
        this.message = message;
        return this;
    }

}