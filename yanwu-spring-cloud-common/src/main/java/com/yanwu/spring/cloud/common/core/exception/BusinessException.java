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

    public static final int EXCEPTIONCODE_DEFAULT = -1;

    // The messageCode is i18n code
    private String messageCode;
    // the messageArgs is arguments for i18n
    private Object[] messageArgs;
    // the exception code defined in system for target exception
    private Integer exceptionCode = EXCEPTIONCODE_DEFAULT;

    /**
     * create new BusinessException
     *
     * @param msg
     *            development message
     * @param messageCode
     *            the i18n code(the message code mapping content defined in
     *            properties)
     */
/*	public BusinessException(String msg, String messageCode) {
		super(msg);
		this.messageCode = messageCode;
	}*/

    /**
     *
     * @param msg
     *            development message
     * @param messageCode
     *            the i18n code(the message code mapping content defined in
     *            properties)
     * @param messageArgs
     *            the i18n arguments
     */
/*	public BusinessException(String msg, String messageCode, Object[] messageArgs) {
		super(msg);
		this.messageCode = messageCode;
		this.messageArgs = messageArgs;
	}*/

    /**
     *
     * @param exceptionCode
     *            The exception code which is defined
     * @param msg
     *            development message
     * @param messageCode
     *            the i18n code(the message code mapping content defined in
     *            properties)
     */
	/*public BusinessException(int exceptionCode, String msg, String messageCode) {
		super(msg);
		this.exceptionCode = exceptionCode;
		this.messageCode = messageCode;
	}*/

    /**
     * @param exceptionCode The exception code which is defined
     * @param messageCode   the i18n code(the message code mapping content defined in
     *                      properties)
     * @param msg           development message
     * @param messageArgs   the i18n arguments
     */
    public BusinessException(Integer exceptionCode, String messageCode, String msg, Object[] messageArgs) {
        super(msg);
        this.exceptionCode = exceptionCode;
        this.messageCode = messageCode;
        this.messageArgs = messageArgs;
    }

    public BusinessException(Integer exceptionCode, String messageCode) {
        super(messageCode);
        this.exceptionCode = exceptionCode;
        this.messageCode = messageCode;
    }

    /**
     * @param msg         development message
     * @param cause       the exception which is catched
     * @param messageCode the i18n code(the message code mapping content defined in
     *                    properties)
     */
    public BusinessException(String msg, Throwable cause, String messageCode) {
        super(msg, cause);
        this.messageCode = messageCode;
    }

    /**
     * @param msg           msg development message
     * @param cause         the exception which is catched
     * @param exceptionCode The exception code which is defined
     * @param messageCode   the i18n code(the message code mapping content defined in
     *                      properties)
     */
    public BusinessException(String msg, Throwable cause, int exceptionCode, String messageCode) {
        super(msg, cause);
        this.exceptionCode = exceptionCode;
        this.messageCode = messageCode;
    }

    /**
     * @param msg         msg development message
     * @param cause       the exception which is catched
     * @param messageCode the i18n code(the message code mapping content defined in
     *                    properties)
     * @param messageArgs the i18n arguments
     */
    public BusinessException(String msg, Throwable cause, String messageCode, Object[] messageArgs) {
        super(msg, cause);
        this.messageCode = messageCode;
        this.messageArgs = messageArgs;
    }

    /**
     * @param msg           msg development message
     * @param cause         the exception which is catched
     * @param exceptionCode The exception code which is defined
     * @param messageCode   the i18n code(the message code mapping content defined in
     *                      properties)
     * @param messageArgs   the i18n arguments
     */
    public BusinessException(String msg, Throwable cause, int exceptionCode, String messageCode, Object[] messageArgs) {
        super(msg, cause);
        this.exceptionCode = exceptionCode;
        this.messageCode = messageCode;
        this.messageArgs = messageArgs;
    }

}
