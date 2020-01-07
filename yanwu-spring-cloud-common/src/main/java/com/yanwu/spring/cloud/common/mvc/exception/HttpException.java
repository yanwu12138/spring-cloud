package com.yanwu.spring.cloud.common.mvc.exception;

import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class HttpException extends RuntimeException {

	private static final long serialVersionUID = -6653537677618518276L;
	private int httpStatusCode;

	public HttpException(int httpStatusCode) {
		super(String.valueOf(httpStatusCode));
		this.httpStatusCode = httpStatusCode;
	}


}
