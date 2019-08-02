package com.yanwu.spring.cloud.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class AutowiringHelper implements ApplicationContextAware {

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(final Class<T> clazz) {
        return (T) beanFactory.autowire(clazz, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
    }

    @SuppressWarnings("unchecked")
    public <T> T getObjectAutowireNo(final Class<T> clazz) {
        return (T) beanFactory.autowire(clazz, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
    }

    public void autowire(final Object bean) {
        beanFactory.autowireBean(bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T autowireAndInitialize(final T bean, final String beanName) {
        autowire(bean);
        return (T) beanFactory.initializeBean(bean, beanName);
    }

}
