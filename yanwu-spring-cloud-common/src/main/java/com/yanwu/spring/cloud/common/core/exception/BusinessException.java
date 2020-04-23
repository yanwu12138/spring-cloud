package com.yanwu.spring.cloud.common.core.exception;

import lombok.Data;

/**
 * This is the exception that used for business exception, this exception class
 * have two more attribute: messageCode and messageArgs
 * <p>
 * messageCode used for i18n as code
 * <p>
 * messageArgs used for i18n as arguments
 */
@Data
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -5480634685747058777L;

    private String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

}
