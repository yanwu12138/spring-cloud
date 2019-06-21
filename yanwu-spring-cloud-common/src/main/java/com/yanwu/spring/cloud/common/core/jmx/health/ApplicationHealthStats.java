package com.yanwu.spring.cloud.common.core.jmx.health;

import javax.management.Notification;

public interface ApplicationHealthStats {

    public final static String POINTER_DOWN = "POINTER_DOWN";

    public final static String POINTER_UP = "POINTER_UP";

    public final static String COMPONENT_DOWN = "COMPONENT_DOWN";

    public final static String COMPONENT_UP = "COMPONENT_UP";

    public final static String APP_DOWN = "APP_DOWN";

    public final static String APP_UP = "APP_UP";

    String getHealth();

    String getAppName();

    String getHealthByComponents(String components);

    String getComponents();

    ApplicationHealth getApplicationHealthByComponents(String components);

    /**
     * Send one notification to JMX subscriber
     *
     * @param notification
     */
    void notify(Notification notification);

}
