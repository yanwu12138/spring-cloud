package com.yanwu.spring.cloud.common.mvc.exception.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement(name = "httpresult")
public class HttpResult {
    
    public static final String HTTP_STATUS_CODE_MAPPING_MESSAGE_PREFIX = "http_status_code_mapping_message_";

    private int httpStatusCode;

    protected String message;

    public HttpResult() {

    }

    public HttpResult(String message) {
        this.httpStatusCode = HttpStatus.OK.value();
        this.message = message;
    }

    public HttpResult(int httpStatusCode, String message) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
    }

}
