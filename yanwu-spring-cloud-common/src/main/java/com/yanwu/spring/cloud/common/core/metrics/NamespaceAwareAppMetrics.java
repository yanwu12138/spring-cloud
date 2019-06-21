package com.yanwu.spring.cloud.common.core.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.JmxAttributeGauge;
import com.codahale.metrics.MetricRegistry;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.yanwu.spring.cloud.common.core.metrics.MetricBuilder.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * The namespace aware AppMetrics implementation.
 * All metrics are under the provided namespace scope, i.e. prefixed by the namespace.
 * <p>
 * This class is designed to be package-private, and be constructed by {@link AppMetricsFactory} static factory methods.
 */
class NamespaceAwareAppMetrics implements AppMetrics {

    private final String namespace;

    private final MetricRegistry metrics;

    public NamespaceAwareAppMetrics(final MetricRegistry metrics, final String namespace) {
        this.metrics = checkNotNull(metrics);
        if (isBlank(namespace)) {
            throw new IllegalArgumentException("The metric namespace must be present");
        }
        this.namespace = namespace;
    }

    MetricRegistry getMetricRegistry() {
        return metrics;
    }

    private String fullMetricName(final String metricName) {
        return name(namespace, metricName);
    }

    @Override
    public void inc(final String metricName) {
        inc(metricName, 1);
    }

    @Override
    public void inc(final String metricName, final long count) {
        metrics.counter(fullMetricName(metricName)).inc(count);
    }

    @Override
    public void dec(final String metricName) {
        dec(metricName, 1);
    }

    @Override
    public void dec(final String metricName, final long count) {
        metrics.counter(fullMetricName(metricName)).dec(count);
    }

    @Override
    public void timing(final String metricName, final long durationInMicros) {
        timing(metricName, durationInMicros, TimeUnit.MICROSECONDS);
    }

    @Override
    public void timing(final String metricName, long duration, final TimeUnit unit) {
        duration = duration < 0 ? 0 : duration; // make sure duration is not negative
        metrics.timer(fullMetricName(metricName)).update(duration, unit);
        inc(name(TOTAL, metricName), unit.toMicros(duration));
    }

    @Override
    public void gauge(final String metricName, final Gauge<?> gauge) {
        String fullName = fullMetricName(metricName);
        try {
            metrics.register(fullName, gauge);
        } catch (IllegalArgumentException e) {  // already registered
            metrics.remove(fullName);
            metrics.register(fullName, gauge);
        }
    }

    @Override
    public void gaugeSystemUptime(final long startupTime) {
        gaugeComponentUptime(SYSTEM, startupTime);
    }

    @Override
    public void gaugeComponentUptime(final String componentName, final long startupTime) {
        gauge(name(componentName, UPTIME), new Gauge<Long>() {

            @Override
            public Long getValue() {
                return (System.currentTimeMillis() - startupTime) / 1000;
            }

        });
    }

    @Override
    public void gaugeJmxAttribute(final String metricName, final String objectName, final String attributeName) {
        Gauge<Object> gauge = null;
        try {
            gauge = new JmxAttributeGauge(new ObjectName(objectName), attributeName);
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException("Invalid JMX Object Name: " + objectName, e);
        }

        gauge(metricName, gauge);
    }

    @Override
    public void gaugeThreadPool(final String threadPoolName, final ThreadPoolExecutor executor) {
        gauge(name(THREAD_POOL_THREADS_ACTIVE, threadPoolName), new Gauge<Integer>() {

            @Override
            public Integer getValue() {
                return executor.getActiveCount();
            }
        });

        gauge(name(THREAD_POOL_SIZE_CURRENT, threadPoolName), new Gauge<Integer>() {

            @Override
            public Integer getValue() {
                return executor.getPoolSize();
            }
        });

        gauge(name(THREAD_POOL_SIZE_LARGEST, threadPoolName), new Gauge<Integer>() {

            @Override
            public Integer getValue() {
                return executor.getLargestPoolSize();
            }
        });

        gauge(name(THREAD_POOL_SIZE_CORE, threadPoolName), new Gauge<Integer>() {

            @Override
            public Integer getValue() {
                return executor.getCorePoolSize();
            }
        });

        gauge(name(THREAD_POOL_SIZE_MAX, threadPoolName), new Gauge<Integer>() {

            @Override
            public Integer getValue() {
                return executor.getMaximumPoolSize();
            }
        });

        gauge(name(THREAD_POOL_TASKS_SCHEDULED, threadPoolName), new Gauge<Long>() {

            @Override
            public Long getValue() {
                return executor.getTaskCount();
            }
        });

        gauge(name(THREAD_POOL_TASKS_COMPLETED, threadPoolName), new Gauge<Long>() {

            @Override
            public Long getValue() {
                return executor.getCompletedTaskCount();
            }
        });
    }

}
