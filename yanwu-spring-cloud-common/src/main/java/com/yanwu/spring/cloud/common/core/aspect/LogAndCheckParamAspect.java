package com.yanwu.spring.cloud.common.core.aspect;

import com.yanwu.spring.cloud.common.core.annotation.CheckFiled;
import com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam;
import com.yanwu.spring.cloud.common.core.exception.ParamException;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author XuBaofeng.
 * @date 2018-11-07 13:49.
 * <p>
 * description:
 */
@Slf4j
@Aspect
@Component
public class LogAndCheckParamAspect {
    private static final String TX_ID = "txId";
    private static final Integer ERROR_PARAM_MAP_SIZE = 0;

    @Pointcut("@annotation(com.yanwu.spring.cloud.common.core.annotation.LogAndCheckParam)")
    public void logAndCheckParamPointcut() {
    }

    @Around("logAndCheckParamPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Method method = getMethodSignature(joinPoint);
        Object[] args = joinPoint.getArgs();
        Map<String, Object> errorParams = new HashMap<>(ERROR_PARAM_MAP_SIZE);
        try {
            log.info("Request   : [txId]: {}, [method]: {}, [param]: {}", getTxId(), method, args);
            LogAndCheckParam annotation = method.getAnnotation(LogAndCheckParam.class);
            CheckFiled[] checks = annotation.check();
            if (ArrayUtils.isNotEmpty(checks)) {
                errorParams = checkParam(checks, args);
                if (MapUtils.isNotEmpty(errorParams)) {
                    throw new ParamException("参数错误, 请检查参数!");
                }
            }
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("Exception : [txId]: {}, [method]: {}, [param]: {}, errorParams: {}", getTxId(), method, args, errorParams, e);
            ResponseEnvelope<Object> envelope = new ResponseEnvelope<>();
            if (e instanceof ParamException) {
                envelope.getResult().setErrorParams(errorParams);
            }
            envelope.getResult().setMessage(e.getMessage());
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
    @AfterReturning(returning = "result", pointcut = "logAndCheckParamPointcut()")
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
     * 校验参数
     *
     * @param checks 注解校验字段
     * @param args   入参
     */
    private Map<String, Object> checkParam(CheckFiled[] checks, Object[] args) {
        Map<String, Object> result = new HashMap<>(ERROR_PARAM_MAP_SIZE);
        Map<String, CheckFiled> checkMap = new HashMap<>(checks.length);
        for (CheckFiled check : checks) {
            checkMap.put(check.field(), check);
        }
        for (Object obj : args) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName();
                field.setAccessible(true);
                if (checkMap.containsKey(name)) {
                    CheckFiled checkFiled = checkMap.get(name);
                    try {
                        String value = String.valueOf(field.get(obj));
                        if (StringUtils.isBlank(value) || !value.matches(checkFiled.regex())) {
                            result.put(name, checkFiled.message());
                        }
                    } catch (Exception e) {
                        result.put(name, checkFiled.message());
                    }
                }
            }
        }
        return result;
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
