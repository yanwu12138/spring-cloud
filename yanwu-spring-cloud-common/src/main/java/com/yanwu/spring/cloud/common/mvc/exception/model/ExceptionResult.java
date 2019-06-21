package com.yanwu.spring.cloud.common.mvc.exception.model;


import com.yanwu.spring.cloud.common.core.exception.BusinessException;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "exception")
public class ExceptionResult extends HttpResult {
    private int exceptionCode = BusinessException.EXCEPTIONCODE_DEFAULT;

    // the message which used for develop
    private String internalMessage;

    public ExceptionResult() {

    }

    public ExceptionResult(int httpStatusCode, String message, String internalMessage) {
        super(httpStatusCode, message);
        this.internalMessage = internalMessage;
    }

    public ExceptionResult(int httpStatusCode, String message, int exceptionCode, String internalMessage) {
        super(httpStatusCode, message);
        this.exceptionCode = exceptionCode;
        this.internalMessage = internalMessage;
    }

    public int getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getInternalMessage() {
        return internalMessage;
    }

    public void setInternalMessage(String internalMessage) {
        this.internalMessage = internalMessage;
    }
}