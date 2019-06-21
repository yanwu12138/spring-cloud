package com.yanwu.spring.cloud.common.core.exception;

public class DeleteException extends DataAccessException {
    private static final long serialVersionUID = -2060960477738285140L;

    public DeleteException(Throwable cause) {
        super(cause);
    }

}
