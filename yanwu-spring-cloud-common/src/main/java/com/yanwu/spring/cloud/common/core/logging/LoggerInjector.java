package com.yanwu.spring.cloud.common.core.logging;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import java.lang.reflect.Field;

/**
 * LoggerInjector is a custom Spring BeanPostProcessor for injecting logger To
 * use this, you can define Logger as follows
 *
 * @Loggable CustomLogger logger;
 */
@Component
public class LoggerInjector implements BeanPostProcessor {

    public LoggerInjector() {
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), new FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                ReflectionUtils.makeAccessible(field);

                // Check if the field is annoted with @Log
                if (field.getAnnotation(Loggable.class) != null) {
                    CustomLogger logger = LoggerFactory.getLogger(bean.getClass());
                    field.set(bean, logger);
                }
            }
        });

        return bean;
    }
}
