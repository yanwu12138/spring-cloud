package com.yanwu.spring.cloud.common.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author Baofeng Xu
 * @date 2022/4/12 16:02.
 * <p>
 * description:
 */
public class AspectUtil {
    private static final String TX_ID = "txId";

    private AspectUtil() {
        throw new UnsupportedOperationException("AspectUtil should never be instantiated");
    }

    /**
     * 获取方法签名
     *
     * @param joinPoint 切点
     * @return 方法
     */
    public static Method getMethodSignature(JoinPoint joinPoint) {
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
    public static String getTxId() {
        HttpServletRequest request = request();
        return request != null ? request.getHeader(TX_ID) : null;
    }

    /**
     * 获取请求信息
     *
     * @return request
     */
    public static HttpServletRequest request() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
