package com.yanwu.spring.cloud.common.core.jmx.health.impl;

import com.yanwu.spring.cloud.common.core.jmx.health.HealthCollector;
import com.yanwu.spring.cloud.common.core.jmx.health.HealthCollectorRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class HealthCollectorRegistryImpl implements HealthCollectorRegistry {

    private Map<String, HealthCollector> collectorsMap = new HashMap<>();

    @Override
    public void register(HealthCollector collector) {
        HealthCollector old = collectorsMap.put(collector.getComponentName(),
                collector);
        if (old != null) {
            log.warn("There is another instance of "
                    + old.getClass() + " about component "
                    + old.getComponentName());
        }
    }

    @Override
    public Collection<HealthCollector> getCollectors() {
        return collectorsMap.values();
    }

    @Override
    public HealthCollector getCollector(String componentName) {
        return collectorsMap.get(componentName);
    }

    @Override
    public Collection<String> getComponents() {
        return collectorsMap.keySet();
    }

}
