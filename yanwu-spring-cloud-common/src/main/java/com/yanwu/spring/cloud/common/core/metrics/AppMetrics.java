package com.yanwu.spring.cloud.common.core.metrics;

import com.codahale.metrics.Gauge;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Application metrics API.
 * The implementation class shall be thread-safe.
 */
public interface AppMetrics {

    /**
     * Increment the {@code counter} metric by one.
     *
     * @param metricName the {@code counter} metric name
     */
    void inc(final String metricName);

    /**
     * Increment the {@code counter} metric by {@code count}.
     *
     * @param metricName the counter metric name
     * @param count      the amount by which the counter will be increased
     */
    void inc(final String metricName, final long count);

    /**
     * Decrement the {@code counter} metric by one.
     *
     * @param metricName the {@code counter} metric name
     */
    void dec(final String metricName);

    /**
     * Decrement the {@code counter} metric by {@code count}.
     *
     * @param metricName the counter metric name
     * @param count      the amount by which the counter will be decreased
     */
    void dec(final String metricName, final long count);

    /**
     * Add duration to a timer metric to aggregates and statistics.
     * <p>
     * The timing not only record the duration meter, but also automatically record a new metric:
     * {@code total.metricName}, because it is the common use case.
     * <p>
     * Negative duration will change to zero.
     *
     * @param metricName       the timer metric name
     * @param durationInMicros duration in microseconds
     */
    void timing(final String metricName, final long durationInMicros);

    /**
     * Add duration to a timer metric to aggregates and statistics.
     * <p>
     * The timing not only record the duration meter, but also automatically record a new metric:
     * {@code total.metricName}, because it is the common use case.
     * <p>
     * Negative duration will change to zero.
     *
     * @param metricName the timer metric name
     * @param duration   duration
     * @param unit       duration time unit
     */
    void timing(final String metricName, final long duration, final TimeUnit unit);

    /**
     * A gauge metric is an instantaneous reading of a particular value.
     *
     * @param metricName the gauge metric name
     * @param gauge      the callback to be implemented
     */
    void gauge(final String metricName, final Gauge<?> gauge);

    /**
     * Gauge the system uptime, the metric name is {@code system.uptime}.
     * <p>
     * The gauge returned system uptime is in {@code SECONDS}.
     *
     * @param startupTime system startup time in milliseconds
     */
    void gaugeSystemUptime(final long startupTime);

    /**
     * Gauge the component uptime, the metric name is {@code componentName.uptime}.
     * <p>
     * The gauge returned system uptime is in {@code SECONDS}.
     *
     * @param componentName the component name
     * @param startupTime   component startup time in milliseconds
     */
    void gaugeComponentUptime(final String componentName, final long startupTime);

    /**
     * Gauge via JMX attribute.
     *
     * @param metricName    the metric name
     * @param objectName    JMX object name
     * @param attributeName JMX attribute name
     */
    void gaugeJmxAttribute(final String metricName, final String objectName, final String attributeName);

    /**
     * Gauge thread pool metrics.
     * The metrics under {@code thread_pool} namespace scope and include below metrics:
     *
     * <pre>
     * thread_pool.threads.active
     * thread_pool.size.current
     * thread_pool.size.largest
     * thread_pool.size.core
     * thread_pool.size.max
     * thread_pool.tasks.scheduled
     * thread_pool.tasks.completed
     * </pre>
     * <p>
     * Every metrics are append {@code threadPoolName} suffix.
     *
     * @param threadPoolName thread pool name
     * @param executor       the ThreadPoolExecutor instance
     */
    void gaugeThreadPool(final String threadPoolName, final ThreadPoolExecutor executor);

}
