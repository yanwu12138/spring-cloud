package com.yanwu.spring.cloud.common.core.aspect;

import com.yanwu.spring.cloud.common.core.annotation.AccessLimit;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.AspectUtil;
import com.yanwu.spring.cloud.common.utils.IpMacUtil;
import com.yanwu.spring.cloud.common.utils.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Baofeng Xu
 * @date 2022/4/12 10:39.
 * <p>
 * description:
 */
@Slf4j
@Aspect
@Component
public class AccessLimitAspect {

    /***
     ** KEY: 方法的名称
     ** VALUE: 多线程安全且键值对是有有效期的Map
     **** KEY: 用户的ID或客户端的IP
     **** VALUE: 单位时间内访问的次数
     */
    private static final Map<String, ExpiringMap<String, Integer>> BOOK = new ConcurrentHashMap<>();

    @Pointcut("@annotation(com.yanwu.spring.cloud.common.core.annotation.AccessLimit)")
    public void accessLimitPointcut() {
    }

    @Around("accessLimitPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String txId = AspectUtil.getTxId();
        Method method = AspectUtil.getMethodSignature(joinPoint);
        AccessLimit annotation = method.getAnnotation(AccessLimit.class);
        try {
            // ===== 拿到请求
            HttpServletRequest request = AspectUtil.request();
            if (request == null) {
                log.error("Exception : [txId]: {}, [method]: {}, [param]: {}. {}", txId, method, args, "the request is empty.");
                return ResponseEnvelope.failed(HttpStatus.INTERNAL_SERVER_ERROR, "内部服务器错误");
            }
            String key = null;
            if (annotation.needLogin()) {
                // ===== 当接口规定用户必须登陆时，则用用户ID作为redis的key
                String token = TokenUtil.getToken(request);
                if (StringUtils.isBlank(token)) {
                    log.error("Exception : [txId]: {}, [method]: {}, [param]: {}. {}", txId, method, args, "the token is empty.");
                    return ResponseEnvelope.failed(HttpStatus.INTERNAL_SERVER_ERROR, "token为空");
                }
                key = String.valueOf(TokenUtil.verifyToken(token).getId());
            }
            if (StringUtils.isBlank(key)) {
                // ===== 当接口规定用户不必登陆时，则用用户IP作为redis的key
                String ip = IpMacUtil.getIpByRequest(request);
                if (StringUtils.isBlank(ip)) {
                    log.error("Exception : [txId]: {}, [method]: {}, [param]: {}. {}", txId, method, args, "could not get IP address in request.");
                    return ResponseEnvelope.failed(HttpStatus.INTERNAL_SERVER_ERROR, "内部服务器错误");
                }
                key = ip;
            }
            // ===== 从缓存中拿到当前用户的访问次数：如果访问次数少于最大限制，则直接放行
            ExpiringMap<String, Integer> eMap = BOOK.getOrDefault(method.getName(), ExpiringMap.builder().variableExpiration().build());
            Integer count = eMap.getOrDefault(request.getRemoteAddr(), 0);
            if (count == 0) {
                eMap.put(key, count + 1, ExpirationPolicy.CREATED, annotation.seconds(), TimeUnit.SECONDS);
            } else if (count < annotation.maxCount()) {
                eMap.put(key, count + 1);
            } else {
                // ===== 如果访问次数已经达到最大限制，则返回请求失败
                log.error("Exception : [txId]: {}, [method]: {}, [param]: {}. {}", txId, method, args, "visit too frequently.");
                return ResponseEnvelope.failed(HttpStatus.INTERNAL_SERVER_ERROR, "访问过于频繁，请稍后再试");
            }
            BOOK.put(method.getName(), eMap);
            return joinPoint.proceed(args);
        } catch (Throwable e) {
            log.error("Exception : [txId]: {}, [method]: {}, [param]: {}", txId, method, args, e);
            return ResponseEnvelope.failed(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
