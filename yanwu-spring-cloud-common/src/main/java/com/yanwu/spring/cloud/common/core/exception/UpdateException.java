package com.yanwu.spring.cloud.common.core.exception;

public class UpdateException extends DataAccessException {
    private static final long serialVersionUID = -2060960477738285140L;

    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(Throwable cause) {
        super(cause);
    }

}
