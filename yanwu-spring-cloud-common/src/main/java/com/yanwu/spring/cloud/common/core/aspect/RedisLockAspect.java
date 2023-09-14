package com.yanwu.spring.cloud.common.core.aspect;

import com.yanwu.spring.cloud.common.core.annotation.RedisLock;
import com.yanwu.spring.cloud.common.core.exception.BusinessException;
import com.yanwu.spring.cloud.common.utils.AspectUtil;
import com.yanwu.spring.cloud.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author XuBaofeng.
 * @date 2023/9/14 20:35.
 * <p>
 * description:
 */
@Slf4j
@Aspect
@Component
public class RedisLockAspect {

    /*** 用于SpEL表达式解析 ***/
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    /*** 用于获取方法参数定义名字 ***/
    private final DefaultParameterNameDiscoverer defaultParameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Resource
    private RedisUtil redisUtil;

    @Pointcut("@annotation(com.yanwu.spring.cloud.common.core.annotation.RedisLock)")
    public void redisLockPointcut() {
    }

    @Around("redisLockPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String txId = AspectUtil.getTxId();
        Method method = AspectUtil.getMethod(joinPoint);
        String className = AspectUtil.getClassName(method);
        String lockKey = null;
        boolean lockFlag = false;
        try {
            lockKey = getRedisLockKey(method.getAnnotation(RedisLock.class), joinPoint);
            if (StringUtils.isBlank(lockKey)) {
                log.error("RedisLock Failed: [txId]: {}, [class]: {}, [method]: {}, [key]: {}", txId, className, method.getName(), lockKey);
                throw new BusinessException("Failed to obtain Redis lock, because lockKey is empty.");
            }
            log.info("RedisLock Success: [txId]: {}, [class]: {}, [method]: {}, [key]: {}", txId, className, method.getName(), lockKey);
            lockFlag = redisUtil.lock(lockKey, Thread.currentThread().getId());
            if (!lockFlag) {
                return joinPoint.proceed();
            } else {
                log.error("RedisLock Failed: [txId]: {}, [class]: {}, [method]: {}, [key]: {}", txId, className, method.getName(), lockKey);
                throw new BusinessException("Failed to obtain Redis lock");
            }
        } finally {
            if (StringUtils.isNotBlank(lockKey) && lockFlag) {
                redisUtil.unLock(lockKey, Thread.currentThread().getId());
                log.info("RedisUnlock Success: [txId]: {}, [class]: {}, [method]: {}, [param]: {}", txId, className, method.getName(), lockKey);
            }
        }
    }

    /***
     * 获取缓存的key
     * @param redisLock 定义在注解上，支持SPEL表达式
     * @param joinPoint 函数
     * @return redis锁的KEY
     */
    public String getRedisLockKey(RedisLock redisLock, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = defaultParameterNameDiscoverer.getParameterNames(methodSignature.getMethod());
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (paramNames != null && paramNames.length > 0) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        return String.valueOf(spelExpressionParser.parseExpression(redisLock.suffix()).getValue(context));
    }

}
