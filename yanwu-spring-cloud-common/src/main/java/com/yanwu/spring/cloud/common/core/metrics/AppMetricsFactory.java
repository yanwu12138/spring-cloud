package com.yanwu.spring.cloud.common.core.metrics;


import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * The factory class to return AppMetrics instance.
 * <p>
 * This class is designed to be thread-safe, and the same argument factory method invocation will always return the same
 * instance.
 *
 * 
 */
public final class AppMetricsFactory {

    /** key: namespace */
    private static final Map<String, AppMetrics> appMetricsMap = new HashMap<>();

    /** key: namespace + interval */
    private static final Map<String, Slf4jReporter> slf4jReporterMap = new HashMap<>();

    private AppMetricsFactory() {}

    /**
     * Returns the period report {@link AppMetrics} instance under the specific {@code namespace}, all metrics will be
     * automatically reported to {@code slf4j} output every specific period.
     * <p>
     * For performance consideration, the minimal report period is {@code one SECONDS}.
     *
     * @param namespace the metric name prefix
     * @param time the time value
     * @param unit the time unit
     * @return period report {@code AppMetrics}
     * @throws IllegalArgumentException if period less than {@code one SECONDS}.
     */
    public static final synchronized AppMetrics getPeriodReportAppMetrics(final String namespace,
                    final long time, final TimeUnit unit) {

        AppMetrics appMetrics = appMetricsMap.get(namespace);
        if (appMetrics == null) {
            MetricRegistry metricRegistry = new MetricRegistry();
            appMetrics = new NamespaceAwareAppMetrics(metricRegistry, namespace);
            appMetricsMap.put(namespace, appMetrics);

            final JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry)
                            .convertRatesTo(TimeUnit.SECONDS)
                            .convertDurationsTo(TimeUnit.MICROSECONDS)
                            .build();
            jmxReporter.start();
        }

        long timeInSeconds = MetricPeriodHelper.toValidateSeconds(time, unit);
        String key = namespace + timeInSeconds;
        if (!slf4jReporterMap.containsKey(key)) {
            final Slf4jReporter slf4jReporter = Slf4jReporter
                            .forRegistry(((NamespaceAwareAppMetrics) appMetrics).getMetricRegistry())
                            .convertRatesTo(TimeUnit.SECONDS)
                            .convertDurationsTo(TimeUnit.MICROSECONDS)
                            .withSuffix(MetricPeriodHelper.toPeriodTag(time, unit))
                            .build();
            slf4jReporter.start(timeInSeconds, TimeUnit.SECONDS);
            slf4jReporterMap.put(key, slf4jReporter);
        }

        return appMetrics;
    }

    /**
     * Returns the {@link AppMetrics} instance under the specific {@code namespace}, all metrics will be automatically
     * reported to {@code slf4j} output every second.
     *
     * @param namespace the metric name prefix
     * @return every second report {@code AppMetrics}
     */
    public static final AppMetrics getSecondlyReportAppMetrics(final String namespace) {
        return getPeriodReportAppMetrics(namespace, 1, TimeUnit.SECONDS);
    }

    /**
     * Returns the {@link AppMetrics} instance under the specific {@code namespace}, all metrics will be automatically
     * reported to {@code slf4j} output every minute.
     *
     * @param namespace the metric name prefix
     * @return every minute report {@code AppMetrics}
     */
    public static final AppMetrics getMinutelyReportAppMetrics(final String namespace) {
        return getPeriodReportAppMetrics(namespace, 1, TimeUnit.MINUTES);
    }

    /**
     * Returns the {@link AppMetrics} instance under the specific {@code namespace}, all metrics will be automatically
     * reported to {@code slf4j} output every hour.
     *
     * @param namespace the metric name prefix
     * @return every hour report {@code AppMetrics}
     */
    public static final AppMetrics getHourlyReportAppMetrics(final String namespace) {
        return getPeriodReportAppMetrics(namespace, 1, TimeUnit.HOURS);
    }

    /**
     * Returns the {@link AppMetrics} instance under the specific {@code namespace}, all metrics will be automatically
     * reported to {@code slf4j} output every day.
     *
     * @param namespace the metric name prefix
     * @return every day report {@code AppMetrics}
     */
    public static final AppMetrics getDailyReportAppMetrics(final String namespace) {
        return getPeriodReportAppMetrics(namespace, 1, TimeUnit.DAYS);
    }

    /**
     * Returns the {@link AppMetrics} instance under the specific {@code namespace}, all metrics will be automatically
     * reported to {@code slf4j} output every week.
     *
     * @param namespace the metric name prefix
     * @return every week report {@code AppMetrics}
     */
    public static final AppMetrics getWeeklyReportAppMetrics(final String namespace) {
        return getPeriodReportAppMetrics(namespace, MetricPeriodHelper.DAYS_WEEK, TimeUnit.DAYS);
    }

}
