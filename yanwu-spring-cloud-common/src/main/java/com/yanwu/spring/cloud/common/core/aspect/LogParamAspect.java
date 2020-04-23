package com.yanwu.spring.cloud.common.core.aspect;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.core.exception.BusinessException;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
    private static final String TX_ID = "txId";

    @Pointcut("@annotation(com.yanwu.spring.cloud.common.core.annotation.LogParam)")
    public void logParamPointcut() {
    }

    @Around("logParamPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Method method = getMethodSignature(joinPoint);
        Object[] args = joinPoint.getArgs();
        try {
            log.info("Request   : [txId]: {}, [method]: {}, [param]: {}", getTxId(), method, args);
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("Exception : [txId]: {}, [method]: {}, [param]: {}", getTxId(), method, args, e);
            String message = "";
            if (e instanceof IllegalArgumentException || e instanceof BusinessException) {
                message = e.getMessage();
            } else {
                LogParam annotation = method.getAnnotation(LogParam.class);
                message = annotation.value();
            }
            ResponseEnvelope<Object> envelope = new ResponseEnvelope<>();
            envelope.getResult().setMessage(message);
            envelope.getResult().setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return new ResponseEntity<>(envelope, HttpStatus.INTERNAL_SERVER_ERROR);
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
        if (result instanceof Serializable || result instanceof ResponseEntity) {
            log.info("Response  : [txId]: {}, [method]: {}, [return]: {}", getTxId(), getMethodSignature(joinPoint), result);
        } else {
            log.info("Response  : [txId]: {}, [method]: {}, [return]: {}", getTxId(), getMethodSignature(joinPoint), "The response could not be serialized.");
        }
    }

    /**
     * 获取方法签名
     *
     * @param joinPoint 切点
     * @return 方法
     */
    private Method getMethodSignature(JoinPoint joinPoint) {
        if (joinPoint == null) {
            return null;
        }
        Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature) {
            return ((MethodSignature) signature).getMethod();
        } else if (signature instanceof AdviceSignature) {
            return ((AdviceSignature) signature).getAdvice();
        } else {
            return null;
        }
    }

    /**
     * 获取每次请求的全局txID
     *
     * @return txId
     */
    private static String getTxId() {
        HttpServletRequest request = request();
        return request != null ? request.getHeader(TX_ID) : null;
    }


    /**
     * 获取请求信息
     *
     * @return request
     */
    private static HttpServletRequest request() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
