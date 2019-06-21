package com.yanwu.spring.cloud.common.core.exception;

public class AuthorizationException extends BusinessException {

    private static final long serialVersionUID = -4120442969532676273L;

    public static final int EXCEPTIONCODE_AUTH                          = -100000;
    public static final int EXCEPTIONCODE_AUTH_DEFAULT                  = -100001;
    public static final int EXCEPTIONCODE_AUTH_TOKEN_ISNULL             = -100002;
    public static final int EXCEPTIONCODE_AUTH_TOKEN_INVALID            = -100003;
    public static final int EXCEPTIONCODE_AUTH_TOKEN_EXPIRED            = -100004;
    public static final int EXCEPTIONCODE_AUTH_TOKEN_GENERATE_ERROR     = -100005;

    public AuthorizationException(String msg, String messageCode) {
        super(EXCEPTIONCODE_AUTH, messageCode, msg, null);
    }

    public AuthorizationException(int exceptionCode, String messageCode, String msg) {
        super(exceptionCode, messageCode, msg, null);
    }

    public AuthorizationException(int exceptionCode, String messageCode, String msg, String... msgArgs) {
        super(exceptionCode, messageCode, msg, msgArgs);
    }

}
