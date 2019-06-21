package com.yanwu.spring.cloud.common.core.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationContextProvider implements ApplicationContextAware {

    private ApplicationContextProvider() {
    }

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("ApplicationContextProvider:setApplicationContext: {}", applicationContext);
        ApplicationContextProvider.applicationContext = applicationContext;
    }

}