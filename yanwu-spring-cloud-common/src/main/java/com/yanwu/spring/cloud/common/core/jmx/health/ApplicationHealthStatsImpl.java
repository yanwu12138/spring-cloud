package com.yanwu.spring.cloud.common.core.jmx.health;

import com.google.common.base.Stopwatch;
import com.yanwu.spring.cloud.common.core.jmx.BaseMBean;
import com.yanwu.spring.cloud.common.core.jmx.JmxNotificationSequence;
import com.yanwu.spring.cloud.common.core.logging.CustomLogger;
import com.yanwu.spring.cloud.common.core.logging.LoggerFactory;
import com.yanwu.spring.cloud.common.core.utils.JsonUtil;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.*;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;
import org.springframework.stereotype.Component;

import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.ObjectName;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * It will be responsibility for collector components health
 */
@Component
@ManagedResource(objectName = ApplicationHealthStatsImpl.OBJECT_NAME,
        description = "Application Health Stats")
public class ApplicationHealthStatsImpl implements ApplicationHealthStats, PropertyChangeListener,
        BaseMBean, InitializingBean, NotificationPublisherAware, SelfNaming {

    public final static String OBJECT_NAME = "com.yanwu.platform.core.jmx.health:name=ApplicationHealthStats";

    final static String DELIMITER = ",";

    private final CustomLogger log = LoggerFactory.getLogger(getClass());

    private String appName = "yanwu";

    @Autowired
    private HealthCollectorRegistry registry;

    @Autowired
    private JmxNotificationSequence sequence;

    @Getter
    private NotificationPublisher publisher;

    private Stopwatch appLaunchedStopwatch;

    private volatile boolean isJmxReady = false;

    private ObjectName objectName;

    private static final String ALL_COMPONENTS = "all";

    @Override
    @ManagedOperation(description = "Get health by all components")
    public String getHealth() {
        return getHealthByComponents(null);
    }

    @Override
    @ManagedOperation(description = "Get health by specified components")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "components", description = "Names of components, the delimiter is ,")})
    public String getHealthByComponents(final String components) {
        ApplicationHealth appHealth = null;
        if (StringUtils.isEmpty(components)) {
            appHealth = getApplicationHealthByComponents(registry.getComponents());
        } else {
            appHealth = getApplicationHealthByComponents(Arrays.asList(components.split(DELIMITER)));
        }
        return JsonUtil.toJsonString(appHealth);
    }

    @Override
    public ApplicationHealth getApplicationHealthByComponents(String components) {
        if (StringUtils.isEmpty(components)) {
            return new ApplicationHealth();
        } else if (ALL_COMPONENTS.equalsIgnoreCase(components)) {
            return getApplicationHealthByComponents(registry.getComponents());
        } else {
            return getApplicationHealthByComponents(Arrays.asList(components.split(DELIMITER)));
        }

    }

    private ApplicationHealth getApplicationHealthByComponents(Collection<String> components) {
        ApplicationHealth appHealth = new ApplicationHealth();
        List<ComponentHealth> compHealthList = new ArrayList<>();
        appHealth.setApplication(getAppName());
        appHealth.setHealthOfComponents(compHealthList);
        if (CollectionUtils.isEmpty(components)) {
            return appHealth;
        }
        boolean hasDown = false;
        for (String component : components) {
            HealthCollector collector = registry.getCollector(component);
            if (collector == null) {
                log.warn("No HealthCollector instance about component {}",
                        component);
                continue;
            }
            ComponentHealth health;
            try {
                health = collector.collect();
            } catch (Exception ex) {
                log.warn("Exception for " + ex.getMessage(), ex);
                health = new ComponentHealth().setComponentName(component).down(ex.getMessage());
            }
            compHealthList.add(health);
            if (!hasDown) {
                hasDown = HealthStatus.DOWN == health.getStatus();
            }
        }
        if (hasDown) {
            appHealth.setStatus(HealthStatus.DOWN);
        }
        return appHealth;

    }

    @Override
    @ManagedAttribute(description = "List all componets of the application")
    public String getComponents() {
        Collection<String> components = registry.getComponents();
        return JsonUtil.toJsonString(components);
    }

    @ManagedAttribute(description = "Application uptime")
    public String getAppUpTime() {
        long duration = appLaunchedStopwatch.elapsed(TimeUnit.MILLISECONDS);
        return DurationFormatUtils.formatDurationWords(duration, true, false);
    }

    @Override
    @ManagedAttribute(description = "Application name")
    public String getAppName() {
        return appName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        appLaunchedStopwatch = Stopwatch.createStarted();
    }

    @Override
    public void setNotificationPublisher(
            final NotificationPublisher notificationPublisher) {
        publisher = notificationPublisher;
        isJmxReady = true;
    }

    @Override
    public void notify(final Notification notification) {
        if (isJmxReady) {
            publisher.sendNotification(notification);
        }
    }

    @Override
    public synchronized ObjectName getObjectName() throws MalformedObjectNameException {
        if (objectName == null) {
            objectName = new ObjectName(ApplicationHealthStatsImpl.OBJECT_NAME + "-" + getAppName());
        }
        return objectName;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (StatusChangeSupport.STATUS.equals(event.getPropertyName())) {
            HealthStatus newStatus = (HealthStatus) event.getNewValue();
            String type = HealthStatus.DOWN == newStatus ? APP_DOWN : APP_UP;
            String message = JsonUtil.toJsonString(event.getSource());

            Notification notification = new Notification(type,
                    getAppName(), sequence.next(), message);
            notify(notification);
        }
    }
}
