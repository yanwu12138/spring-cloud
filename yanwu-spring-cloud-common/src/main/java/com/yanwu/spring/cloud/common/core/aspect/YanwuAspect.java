package com.yanwu.spring.cloud.common.core.aspect;

import com.yanwu.spring.cloud.common.core.annotation.CheckFiled;
import com.yanwu.spring.cloud.common.core.annotation.LogAndParam;
import com.yanwu.spring.cloud.common.mvc.res.ResponseEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import java.lang.reflect.Field;
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
public class YanwuAspect {

    @Pointcut("@annotation(com.yanwu.spring.cloud.common.core.annotation.LogAndParam)")
    public void yanwuPointcut() {
    }

    @Around("yanwuPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Method method = getMethodSignature(joinPoint);
        Object[] args = joinPoint.getArgs();
        log.info("Request   : [method]: {}, [param]: {}", method, args);
        try {
            LogAndParam logAndParam = method.getAnnotation(LogAndParam.class);
            if (logAndParam.check()) {
                checkParam(args);
            }
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("Exception : [method]: {}, [param]: {}", method, args, e);
            return new ResponseEntity<>(new ResponseEnvelope<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Ø
     * 输出所有controller方法的出参
     *
     * @param joinPoint
     * @param result
     */
    @AfterReturning(returning = "result", pointcut = "yanwuPointcut()")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Response  : [method]: {}, [return]: {}", getMethodSignature(joinPoint), result);
    }

    /**
     * 获取方法签名
     *
     * @param joinPoint
     * @return
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
     * 校验参数
     *
     * @param args
     */
    private void checkParam(Object[] args) {
        for (Object obj : args) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                CheckFiled checkFiled = field.getAnnotation(CheckFiled.class);
                if (checkFiled == null) {
                    continue;
                }
                field.setAccessible(true);
                String value;
                try {
                    value = (String) field.get(obj);
                } catch (Exception e) {
                    log.error("check param aspect get filed value error!", e);
                    continue;
                }
                if (StringUtils.isBlank(value) || !value.matches(checkFiled.regex())) {
                    throw new RuntimeException(checkFiled.message());
                }
            }
        }
    }

}
