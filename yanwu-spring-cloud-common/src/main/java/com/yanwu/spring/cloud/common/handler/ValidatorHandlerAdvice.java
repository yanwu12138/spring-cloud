package com.yanwu.spring.cloud.common.handler;

import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/22 19:13.
 * <p>
 * description:
 */
@RestControllerAdvice
public class ValidatorHandlerAdvice<T> {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleBindException(MethodArgumentNotValidException exception) {
        ResponseEnvelope<T> envelope = new ResponseEnvelope<>();
        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            envelope.getResult().setMessage(error.getDefaultMessage());
        }
        envelope.getResult().setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(envelope, HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

}
