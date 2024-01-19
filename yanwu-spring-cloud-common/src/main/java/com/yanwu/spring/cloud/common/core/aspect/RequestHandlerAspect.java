package com.yanwu.spring.cloud.common.core.aspect;

import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import com.yanwu.spring.cloud.common.core.common.Contents;
import com.yanwu.spring.cloud.common.core.enums.AccessTypeEnum;
import com.yanwu.spring.cloud.common.core.exception.BusinessException;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.pojo.UserAccessesInfo;
import com.yanwu.spring.cloud.common.utils.AspectUtil;
import com.yanwu.spring.cloud.common.utils.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.util.HashSet;
import java.util.Set;

/**
 * @author XuBaofeng.
 * @date 2018-11-07 13:49.
 * <p>
 * description:
 */
@Slf4j
@Aspect
@Component
public class RequestHandlerAspect {
    private static final ThreadLocal<UserAccessesInfo> USER_ACCESSES_LOCAL = new ThreadLocal<>();

    @Pointcut("@annotation(com.yanwu.spring.cloud.common.core.annotation.RequestHandler)")
    public void logParamPointcut() {
    }

    @Around("logParamPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String txId = AspectUtil.getTxId();
        Method method = AspectUtil.getMethod(joinPoint);
        try {
            log.info("Request: [txId]: {}, [class]: {}, [method]: {}, [param]: {}",
                    txId, AspectUtil.getClassName(method), method.getName(), AspectUtil.printArgs(args));
            RequestHandler requestHandler = method.getAnnotation(RequestHandler.class);
            if (requestHandler == null || requestHandler.dataScope() == null) {
                // ----- 不进行数据权限过滤
                return joinPoint.proceed(args);
            }
            String token = ContextUtil.header(Contents.TOKEN);
            if (StringUtils.isBlank(token)) {
                throw new BusinessException("无数据权限");
            }
            // ----- 根据TOKEN获取有权限的数据ID集合，放入ThreadLocal中等待使用
            UserAccessesInfo instanceInfo = UserAccessesInfo.getInstance(requestHandler);
            if (requestHandler.dataScope().shop()) {
                Set<Long> userIds = buildDataIds(token, AccessTypeEnum.USER);
                if (CollectionUtils.isEmpty(userIds)) {
                    throw new BusinessException("无数据权限");
                }
                instanceInfo.setUserIds(userIds);
            }
            if (requestHandler.dataScope().agent()) {
                Set<Long> roleIds = buildDataIds(token, AccessTypeEnum.ROLE);
                if (CollectionUtils.isEmpty(roleIds)) {
                    throw new BusinessException("无数据权限");
                }
                instanceInfo.setRoleIds(roleIds);
            }
            USER_ACCESSES_LOCAL.set(instanceInfo);
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("Exception: [txId]: {}, [class]: {}, [method]: {}, [param]: {}",
                    txId, AspectUtil.getClassName(method), method.getName(), AspectUtil.printArgs(args), e);
            String message;
            if (e instanceof IllegalArgumentException || e instanceof BusinessException) {
                message = e.getMessage();
            } else {
                RequestHandler annotation = method.getAnnotation(RequestHandler.class);
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
        Method method = AspectUtil.getMethod(joinPoint);
        if (result instanceof Serializable || result instanceof ResponseEntity) {
            log.info("Response: [txId]: {}, [class]: {}, [method]: {}, [return]: {}",
                    txId, AspectUtil.getClassName(method), method.getName(), AspectUtil.print(result));
        } else {
            log.info("Response: [txId]: {}, [class]: {}, [method]: {}, [return]: {}",
                    txId, AspectUtil.getClassName(method), method.getName(), "The response could not be serialized.");
        }
    }

    /**
     * 根据token和需要鉴权的数据类型来获取该类型的数据权限
     *
     * @param token      token
     * @param accessType 类型
     */
    private Set<Long> buildDataIds(String token, AccessTypeEnum accessType) {
        // ***** TODO 根据token和需要鉴权的数据类型来获取该类型的数据权限
        return new HashSet<>();
    }

}
