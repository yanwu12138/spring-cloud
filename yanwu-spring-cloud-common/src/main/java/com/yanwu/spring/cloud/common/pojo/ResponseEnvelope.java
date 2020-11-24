package com.yanwu.spring.cloud.common.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class ResponseEnvelope<T> implements Serializable {
    private static final long serialVersionUID = 5713406382349859603L;

    /*** 数据 ***/
    private T data;
    /*** 接口返回状态 ***/
    private Boolean status;
    /*** 接口错误码 ***/
    private Integer code;
    /*** 接口错误提示语 ***/
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

}