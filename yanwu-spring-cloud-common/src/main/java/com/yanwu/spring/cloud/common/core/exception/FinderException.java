package com.yanwu.spring.cloud.common.core.exception;

public class FinderException extends DataAccessException {
    private static final long serialVersionUID = -2060960477738285140L;

    public FinderException(String message) {
        super(message);
    }

    public FinderException(Throwable cause) {
        super(cause);
    }

}
