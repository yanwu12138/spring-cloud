package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-02 13:56.
 * <p>
 * description:
 */
@Slf4j
@Component
@SuppressWarnings("unused")
public class ContextUtil implements ApplicationContextAware {

    private ContextUtil() {
    }

    private static ApplicationContext context = null;

    public static ApplicationContext getContext() {
        return context;
    }

    @Override
    public void setApplicationContext(@NonNull final ApplicationContext applicationContext) throws BeansException {
        log.info("ContextUtil: setApplicationContext: {}", applicationContext);
        ContextUtil.context = applicationContext;
    }

    public static Object getBean(String beanName) {
        if (context == null || StringUtils.isBlank(beanName)) {
            return null;
        }
        return context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        if (context == null || clazz == null) {
            return null;
        }
        return context.getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        if (context == null || StringUtils.isBlank(beanName) || clazz == null) {
            return null;
        }
        return context.getBean(beanName, clazz);
    }

    public static Set<Class<?>> getClazzByAnnotation(Class<? extends Annotation> annotation) {
        Map<String, Object> beans = context.getBeansWithAnnotation(annotation);
        if (MapUtils.isEmpty(beans)) {
            return Collections.emptySet();
        }
        Set<Class<?>> classes = new HashSet<>();
        beans.values().forEach(bean -> classes.add(bean.getClass()));
        return classes;
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

    /**
     * 获取请求头中携带的信息
     *
     * @param key KEY
     * @return VALUE
     */
    public static String header(String key) {
        HttpServletRequest request = request();
        return request == null ? "" : request.getHeader(key);
    }

}