package com.yanwu.spring.cloud.common.core.aspect;

import com.yanwu.spring.cloud.common.core.annotation.YanwuLog;
import com.yanwu.spring.cloud.common.mvc.res.BackVO;
import com.yanwu.spring.cloud.common.utils.BackVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.yanwu.spring.cloud.common.core.exception.ExceptionDefinition.SYSTEM_ERROR;

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

    @Pointcut("@annotation(com.yanwu.spring.cloud.common.core.annotation.YanwuLog)")
    public void yanwuPointcut() {
    }

    @Around("yanwuPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Method method = getMethodSignature(joinPoint);
        Object[] args = joinPoint.getArgs();
        try {
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("Exception : [method]: {}, [param]: {}", method, args, e);
            return getBackVO(method);
        }
    }

    /**
     * 输出所有controller方法的入参
     *
     * @param joinPoint
     */
    @Before("yanwuPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Method method = getMethodSignature(joinPoint);
        log.info("Request   : [method]: {}, [param]: {}", method, args);
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
        Object[] args = joinPoint.getArgs();
        Method method = getMethodSignature(joinPoint);
        log.info("Response  : [method]: {}, [param]: {}, [return]: {}", method, args, result);
    }

    /**
     * 输出所有controller的异常信息
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "yanwuPointcut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Object[] args = joinPoint.getArgs();
        Method method = getMethodSignature(joinPoint);
        log.error("Exception : [method]: {}, [param]: {}", method, args, e);
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
     * 根据 @YanwuLog 注解的 value 值组装返回的BackVO
     *
     * @param method
     * @return
     */
    private BackVO getBackVO(Method method) {
        String key = SYSTEM_ERROR.key;
        YanwuLog yanwuLog = method.getAnnotation(YanwuLog.class);
        if (yanwuLog != null) {
            key = yanwuLog.value();
        }
        return BackVOUtil.operateError(SYSTEM_ERROR.code, key);
    }

}
