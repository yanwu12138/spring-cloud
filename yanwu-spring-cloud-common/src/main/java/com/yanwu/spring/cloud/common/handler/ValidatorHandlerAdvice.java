package com.yanwu.spring.cloud.common.handler;

import com.yanwu.spring.cloud.common.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/22 19:13.
 * <p>
 * description: 全局参数校验异常处理类
 */
@Slf4j
@RestControllerAdvice
public class ValidatorHandlerAdvice<T> {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected Result<T> handleBindException(MethodArgumentNotValidException exception) {
        Result<T> envelope = Result.failed();
        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            envelope = Result.failed(error.getDefaultMessage());
        }
        log.error("Validator Handler Advice Exception: ", exception);
        return envelope;
    }

}
