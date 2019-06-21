package com.yanwu.spring.cloud.common.core.exception;

public class CreateException extends DataAccessException {
    private static final long serialVersionUID = -2060960477738285140L;

    public CreateException(Throwable cause) {
        super(cause);
    }

}
