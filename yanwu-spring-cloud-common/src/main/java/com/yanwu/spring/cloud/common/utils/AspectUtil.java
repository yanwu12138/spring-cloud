package com.yanwu.spring.cloud.common.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
    public static Method getMethod(JoinPoint joinPoint) {
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

    public static String getSignature(Method method) {
        RequestMapping requestMapping = method.getDeclaringClass().getAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            return method.getName();
        }
        String[] value = requestMapping.value();
        if (value.length == 0) {
            return method.getName();
        }
        return value[0] + method.getName();
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
     * 获取每次请求的全局txID
     *
     * @return ClassName
     */
    public static String getClassName(Method method) {
        return method.getDeclaringClass().getSimpleName();
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

    public static String print(Object obj) {
        return JsonUtil.toString(obj, Boolean.TRUE);
    }

    public static String printArgs(Object[] args) {
        List<Object> result = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof HttpServletResponse) {
                continue;
            }
            if (arg instanceof HttpServletRequest) {
                continue;
            }
            if (arg instanceof Part) {
                continue;
            }
            if (arg instanceof BindingResult) {
                continue;
            }
            if (arg instanceof File) {
                continue;
            }
            if (arg instanceof InputStream) {
                continue;
            }
            if (arg instanceof OutputStream) {
                continue;
            }
            result.add(arg);
        }
        return print(result);
    }

}
