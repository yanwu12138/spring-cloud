package com.yanwu.spring.cloud.common.core.exception;

public class DataAccessException extends BusinessException {
    private static final long serialVersionUID = 354189252276780265L;

    public DataAccessException(String message) {
        super(BusinessException.EXCEPTIONCODE_DEFAULT, "DataAccessException", message, null);
    }

    public DataAccessException(Throwable cause) {
        super("DataAccessException", cause, "DataAccessException");
    }

}
