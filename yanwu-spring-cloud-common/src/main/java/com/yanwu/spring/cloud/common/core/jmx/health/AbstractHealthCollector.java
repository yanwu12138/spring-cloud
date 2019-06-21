package com.yanwu.spring.cloud.common.core.jmx.health;

import com.yanwu.spring.cloud.common.core.jmx.JmxNotificationSequence;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.Notification;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public abstract class AbstractHealthCollector implements PropertyChangeListener,
        HealthCollector, InitializingBean {

    /**
     * Do not create new instance of <code>ComponentHealth</code>, re-use following
     */
    final protected ComponentHealth health = new ComponentHealth();

    private final ConcurrentMap<String, Object> faultPointerMap = new ConcurrentHashMap<>();

    @Autowired
    private HealthCollectorRegistry registry;
    @Autowired
    private ApplicationHealthStats healthStats;

    @Autowired
    private JmxNotificationSequence sequence;

    @Override
    public void afterPropertiesSet() throws Exception {
        registry.register(this);
        health.setComponentName(getComponentName());
        health.addPropertyChangeListener(this);
    }

    /**
     * Extract default component name from class name
     */
    @Override
    public String getComponentName() {
        String className = getClass().getSimpleName();
        int index = className.indexOf("HealthCollector");
        return index < 0 ? className : className.substring(0, index);
    }

    @Override
    public ComponentHealth collect() {
        if (!faultPointerMap.isEmpty()) {
            Collection<Object> values = faultPointerMap.values();
            health.down(values.toString());
        } else {
            health.up();
        }

        return health;
    }

    @Override
    public void raise(@NonNull String pointerId, @NonNull Object message) {
        if (faultPointerMap.containsKey(pointerId)) {
            return;
        }

        faultPointerMap.put(pointerId, message);
        log.warn("{}.{} is Down", getComponentName(), pointerId);

        Notification notification = new Notification(ApplicationHealthStats.POINTER_DOWN,
                getComponentName() + "." + pointerId,
                sequence.next(),
                message.toString());
        healthStats.notify(notification);

        health.down(faultPointerMap.values().toString());
    }

    @Override
    public void clear(@NonNull String pointerId) {
        if (!faultPointerMap.containsKey(pointerId)) {
            return;
        }

        log.warn("{}.{} is Up", getComponentName(), pointerId);
        faultPointerMap.remove(pointerId);

        Notification notification = new Notification(ApplicationHealthStats.POINTER_UP,
                getComponentName() + "." + pointerId,
                sequence.next(),
                "");
        healthStats.notify(notification);

        if (faultPointerMap.isEmpty()) {
            health.up();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (StatusChangeSupport.STATUS.equals(event.getPropertyName())) {
            HealthStatus newStatus = (HealthStatus) event.getNewValue();
            String type = HealthStatus.DOWN == newStatus ? ApplicationHealthStats.COMPONENT_DOWN : ApplicationHealthStats.COMPONENT_UP;

            String message = health.getMessage();
            if (!faultPointerMap.isEmpty()) {
                message = faultPointerMap.values().toString();
            }

            Notification notification = new Notification(type,
                    getComponentName(), sequence.next(), message);
            healthStats.notify(notification);
        }
    }

}