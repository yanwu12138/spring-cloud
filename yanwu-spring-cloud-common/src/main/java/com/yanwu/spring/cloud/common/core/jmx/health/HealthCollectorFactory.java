package com.yanwu.spring.cloud.common.core.jmx.health;

import com.yanwu.spring.cloud.common.core.utils.AutowiringHelper;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class HealthCollectorFactory implements FactoryBean<HealthCollector>, InitializingBean {

    @Setter
    private String componentName;

    private HealthCollector collector;

    @Autowired
    private AutowiringHelper wiringHelper;

    @Override
    public HealthCollector getObject() throws Exception {
        return collector;
    }

    @Override
    public Class<?> getObjectType() {
        return HealthCollector.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        collector = wiringHelper.getObject(DefaultHealthCollector.class);
        ((DefaultHealthCollector)collector).setComponentName(componentName);
        ((DefaultHealthCollector)collector).afterPropertiesSet();
    }
}
