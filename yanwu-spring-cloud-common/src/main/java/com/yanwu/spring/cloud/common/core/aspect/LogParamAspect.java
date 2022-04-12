package com.yanwu.spring.cloud.common.core.aspect;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.core.exception.BusinessException;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.AspectUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author XuBaofeng.
 * @date 2018-11-07 13:49.
 * <p>
 * description:
 */
@Slf4j
@Aspect
@Component
public class LogParamAspect {

    @Pointcut("@annotation(com.yanwu.spring.cloud.common.core.annotation.LogParam)")
    public void logParamPointcut() {
    }

    @Around("logParamPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String txId = AspectUtil.getTxId();
        Method method = AspectUtil.getMethodSignature(joinPoint);
        try {
            log.info("Request   : [txId]: {}, [method]: {}, [param]: {}", txId, method, args);
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("Exception : [txId]: {}, [method]: {}, [param]: {}", txId, method, args, e);
            String message = "";
            if (e instanceof IllegalArgumentException || e instanceof BusinessException) {
                message = e.getMessage();
            } else {
                LogParam annotation = method.getAnnotation(LogParam.class);
                message = annotation.value();
            }
            return ResponseEnvelope.failed(HttpStatus.INTERNAL_SERVER_ERROR, message);
        }
    }

    /**
     * 输出所有controller方法的出参
     *
     * @param joinPoint 切点
     * @param result    返回值
     */
    @AfterReturning(returning = "result", pointcut = "logParamPointcut()")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        String txId = AspectUtil.getTxId();
        Method method = AspectUtil.getMethodSignature(joinPoint);
        if (result instanceof Serializable || result instanceof ResponseEntity) {
            log.info("Response  : [txId]: {}, [method]: {}, [return]: {}", txId, method, result);
        } else {
            log.info("Response  : [txId]: {}, [method]: {}, [return]: {}", txId, method, "The response could not be serialized.");
        }
    }

}
