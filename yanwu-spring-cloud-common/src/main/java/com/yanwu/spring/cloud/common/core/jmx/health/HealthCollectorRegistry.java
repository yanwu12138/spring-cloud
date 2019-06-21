package com.yanwu.spring.cloud.common.core.jmx.health;

import java.util.Collection;

public interface HealthCollectorRegistry {

    /**
     * auto-register instance of <code>HealthCollector</code>
     *
     * @param collector
     */
    void register(HealthCollector collector);

    /**
     * @return All instances of <code>HealthCollector</code>
     */
    Collection<HealthCollector> getCollectors();

    /**
     * @param componentName
     * @return The collector with specified component name
     */
    HealthCollector getCollector(String componentName);

    /**
     * @return All component names
     */
    Collection<String> getComponents();

}
