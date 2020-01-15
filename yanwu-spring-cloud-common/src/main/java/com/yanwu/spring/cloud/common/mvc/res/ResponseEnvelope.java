package com.yanwu.spring.cloud.common.mvc.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author Administrator
 */
@ToString
@JsonInclude(Include.NON_NULL)
public class ResponseEnvelope<T> implements Serializable {
    private static final long serialVersionUID = 5713406382349859603L;
    @Getter
    private T data;
    @Getter
    private PaginationInfo pagination;
    @Getter
    private RestApiError result;

    public ResponseEnvelope() {
        this(null, null);
    }

    public ResponseEnvelope(T data) {
        this(data, null);
    }

    public ResponseEnvelope(T data, PaginationInfo pagination) {
        this.data = data;
        this.pagination = pagination;

        result = new RestApiError();
        result.setStatus(HttpStatus.OK.value());
        result.setCode(RestApiError.CODE_OK);
        result.setMessage(RestApiError.MESSAGE_SUCCEED);
        result.setErrorParams(null);
    }

    public ResponseEnvelope(RestApiError error) {
        this.result = error;
    }

}