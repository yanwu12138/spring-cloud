package com.yanwu.spring.cloud.common.core.exception;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020-01-15 14:29.
 * <p>
 * description:
 */
public class ParamException extends RuntimeException {
    private static final long serialVersionUID = 6950130218551404530L;
    private String message;

    public ParamException(String message) {
        super(message);
        this.message = message;
    }

    public ParamException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }

}
