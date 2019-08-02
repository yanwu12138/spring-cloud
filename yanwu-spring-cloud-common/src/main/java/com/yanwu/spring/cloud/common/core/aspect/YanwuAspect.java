package com.yanwu.spring.cloud.common.core.aspect;

import com.yanwu.spring.cloud.common.core.annotation.CheckParam;
import com.yanwu.spring.cloud.common.core.enums.CheckEnum;
import com.yanwu.spring.cloud.common.utils.ArrayUtil;
import com.yanwu.spring.cloud.common.utils.CheckParamUtil;
import com.yanwu.spring.cloud.common.mvc.req.BaseParam;
import com.yanwu.spring.cloud.common.mvc.res.BackVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

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

    @Pointcut("@annotation(com.yanwu.spring.cloud.common.core.annotation.YanwuLog) || " +
            "@annotation(com.yanwu.spring.cloud.common.core.annotation.CheckParam)")
    public void yanwuPointcut() {
    }

    @Around("yanwuPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        String traceId;
        BaseParam param;
        Method method = getMethodSignature(joinPoint);
        Annotation[] annotations = method.getAnnotations();
        Object[] args = joinPoint.getArgs();
        try {
            if (ArrayUtil.isNotEmpty(args)) {
                for (Object arg : args) {
                    if (arg instanceof BaseParam) {
                        param = (BaseParam) arg;
                        if (StringUtils.isBlank(param.getTraceId())) {
                            traceId = UUID.randomUUID().toString().replaceAll("-", "");
                            param.setTraceId(traceId);
                        }
                        // ===== 校验参数
                        checkParam(param, annotations);
                    }
                }
            }
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            traceId = getTraceId(joinPoint);
            log.error("Exception : [traceId]: {}, [method]: {}, [param]: {}", traceId, method, args, e);
            return new BackVO<>(SYSTEM_ERROR.code, SYSTEM_ERROR.key, traceId);
        }
    }

    /**
     * 输出所有controller方法的入参
     *
     * @param joinPoint
     */
    @Before("yanwuPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        String traceId = getTraceId(joinPoint);
        Object[] args = joinPoint.getArgs();
        Method method = getMethodSignature(joinPoint);
        log.info("Request   : [traceId]: {}, [method]: {}, [param]: {}", traceId, method, args);
    }

    /**
     * 输出所有controller方法的出参
     *
     * @param joinPoint
     * @param result
     */
    @AfterReturning(returning = "result", pointcut = "yanwuPointcut()")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        String traceId = getTraceId(joinPoint);
        Object[] args = joinPoint.getArgs();
        Method method = getMethodSignature(joinPoint);
        log.info("Response  : [traceId]: {}, [method]: {}, [param]: {}, [return]: {}", traceId, method, args, result);
    }

    /**
     * 输出所有controller的异常信息
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "yanwuPointcut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String traceId = getTraceId(joinPoint);
        Object[] args = joinPoint.getArgs();
        Method method = getMethodSignature(joinPoint);
        log.error("Exception : [traceId]: {}, [method]: {}, [param]: {}", traceId, method, args, e);
    }

    /**
     * 参数校验
     *
     * @param param
     * @param annotations
     * @throws Exception
     */
    private void checkParam(BaseParam param, Annotation[] annotations) throws Exception {
        for (Annotation annotation : annotations) {
            if (annotation instanceof CheckParam) {
                CheckEnum check = ((CheckParam) annotation).check();
                switch (check) {
                    case STRING_NOT_BLANK:
                        CheckParamUtil.checkStringNotBlank((String) param.getData());
                        break;
                    case LONG_GREATER_THAN_ZERO:
                        CheckParamUtil.checkLongNotThanZero((Long) param.getData());
                        break;
                    case LIST_NOT_EMPTY:
                        CheckParamUtil.checkListNotNullAndSizeGreaterZero((List) param.getData());
                        break;
                    case DATA_NOT_NULL:
                    default:
                        CheckParamUtil.checkObjectNotNull(param.getData());
                        break;
                }
            }
        }
    }

    /**
     * 获取TraceId
     *
     * @param joinPoint
     * @return
     */
    private String getTraceId(JoinPoint joinPoint) {
        String traceId = "";
        Object[] args = joinPoint.getArgs();
        if (ArrayUtil.isNotEmpty(args)) {
            for (Object arg : args) {
                if (arg instanceof BaseParam) {
                    traceId = ((BaseParam) arg).getTraceId();
                }
            }
        }
        return traceId;
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

}
