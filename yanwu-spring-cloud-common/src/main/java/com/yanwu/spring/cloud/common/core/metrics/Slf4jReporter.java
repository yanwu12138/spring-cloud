package com.yanwu.spring.cloud.common.core.metrics;

import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class Slf4jReporter extends ScheduledReporter {

    /**
     * Returns a new {@link Builder} for {@link Slf4jReporter}.
     *
     * @param registry the registry to report
     * @return a {@link Builder} instance for a {@link Slf4jReporter}
     */
    public static Builder forRegistry(final MetricRegistry registry) {
        return new Builder(registry);
    }

    public enum LoggingLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    /**
     * A builder for {@link CsvReporter} instances. Defaults to logging to {@code metrics}, not
     * using a marker, converting rates to events/second, converting durations to milliseconds, and
     * not filtering metrics.
     */
    public static class Builder {

        private final MetricRegistry registry;
        private Logger logger;
        private LoggingLevel loggingLevel;
        private Marker marker;
        private TimeUnit rateUnit;
        private TimeUnit durationUnit;
        private MetricFilter filter;
        private String suffix;

        private Builder(final MetricRegistry registry) {
            this.registry = registry;
            logger = LoggerFactory.getLogger("metrics");
            marker = null;
            rateUnit = TimeUnit.SECONDS;
            durationUnit = TimeUnit.MILLISECONDS;
            filter = MetricFilter.ALL;
            loggingLevel = LoggingLevel.INFO;
        }

        /**
         * Log metrics to the given logger.
         *
         * @param logger an SLF4J {@link Logger}
         * @return {@code this}
         */
        public Builder outputTo(final Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Mark all logged metrics with the given marker.
         *
         * @param marker an SLF4J {@link Marker}
         * @return {@code this}
         */
        public Builder markWith(final Marker marker) {
            this.marker = marker;
            return this;
        }

        /**
         * Convert rates to the given time unit.
         *
         * @param rateUnit a unit of time
         * @return {@code this}
         */
        public Builder convertRatesTo(final TimeUnit rateUnit) {
            this.rateUnit = rateUnit;
            return this;
        }

        /**
         * Convert durations to the given time unit.
         *
         * @param durationUnit a unit of time
         * @return {@code this}
         */
        public Builder convertDurationsTo(final TimeUnit durationUnit) {
            this.durationUnit = durationUnit;
            return this;
        }

        /**
         * Only report metrics which match the given filter.
         *
         * @param filter a {@link MetricFilter}
         * @return {@code this}
         */
        public Builder filter(final MetricFilter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Use Logging Level when reporting.
         *
         * @param loggingLevel a (@link HiveSlf4jReporter.LoggingLevel}
         * @return {@code this}
         */
        public Builder withLoggingLevel(final LoggingLevel loggingLevel) {
            this.loggingLevel = loggingLevel;
            return this;
        }

        /**
         * Append suffix to metric name when reporting.
         *
         * @param loggingLevel a (@link HiveSlf4jReporter.LoggingLevel}
         * @return {@code this}
         */
        public Builder withSuffix(final String suffix) {
            this.suffix = suffix;
            return this;
        }

        /**
         * Builds a {@link Slf4jReporter} with the given properties.
         *
         * @return a {@link Slf4jReporter}
         */
        public Slf4jReporter build() {
            LoggerProxy loggerProxy;
            switch (loggingLevel) {
                case TRACE:
                    loggerProxy = new TraceLoggerProxy(logger);
                    break;
                case INFO:
                    loggerProxy = new InfoLoggerProxy(logger);
                    break;
                case WARN:
                    loggerProxy = new WarnLoggerProxy(logger);
                    break;
                case ERROR:
                    loggerProxy = new ErrorLoggerProxy(logger);
                    break;
                default:
                case DEBUG:
                    loggerProxy = new DebugLoggerProxy(logger);
                    break;
            }
            return new Slf4jReporter(registry, loggerProxy, marker, rateUnit, durationUnit, filter, suffix);
        }
    }

    private final LoggerProxy loggerProxy;
    private final Marker marker;
    private final String suffix;

    private Slf4jReporter(final MetricRegistry registry,
                          final LoggerProxy loggerProxy,
                          final Marker marker,
                          final TimeUnit rateUnit,
                          final TimeUnit durationUnit,
                          final MetricFilter filter,
                          final String suffix) {
        super(registry, "logger-reporter", filter, rateUnit, durationUnit);
        this.loggerProxy = loggerProxy;
        this.marker = marker;
        this.suffix = suffix;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void report(final SortedMap<String, Gauge> gauges,
                       final SortedMap<String, Counter> counters,
                       final SortedMap<String, Histogram> histograms,
                       final SortedMap<String, Meter> meters,
                       final SortedMap<String, Timer> timers) {
        for (Entry<String, Gauge> entry : gauges.entrySet()) {
            logGauge(metricName(entry.getKey()), entry.getValue());
        }

        for (Entry<String, Counter> entry : counters.entrySet()) {
            logCounter(metricName(entry.getKey()), entry.getValue());
        }

        for (Entry<String, Histogram> entry : histograms.entrySet()) {
            logHistogram(metricName(entry.getKey()), entry.getValue());
        }

        for (Entry<String, Meter> entry : meters.entrySet()) {
            logMeter(metricName(entry.getKey()), entry.getValue());
        }

        for (Entry<String, Timer> entry : timers.entrySet()) {
            logTimer(metricName(entry.getKey()), entry.getValue());
        }
    }

    /**
     * Add suffix if necessary.
     *
     * @param name original metric name
     * @return changed metric name
     */
    private String metricName(final String name) {
        return suffix == null ? name : name + "." + suffix;
    }

    private void logTimer(final String name, final Timer timer) {
        final Snapshot snapshot = timer.getSnapshot();
        loggerProxy.log(marker,
                "type=TIMER, name={}, count={}, min={}, max={}, mean={}, stddev={}, median={}, " +
                        "p75={}, p95={}, p98={}, p99={}, p999={}, mean_rate={}, m1={}, m5={}, " +
                        "m15={}, rate_unit={}, duration_unit={}",
                name,
                timer.getCount(),
                convertDuration(snapshot.getMin()),
                convertDuration(snapshot.getMax()),
                convertDuration(snapshot.getMean()),
                convertDuration(snapshot.getStdDev()),
                convertDuration(snapshot.getMedian()),
                convertDuration(snapshot.get75thPercentile()),
                convertDuration(snapshot.get95thPercentile()),
                convertDuration(snapshot.get98thPercentile()),
                convertDuration(snapshot.get99thPercentile()),
                convertDuration(snapshot.get999thPercentile()),
                convertRate(timer.getMeanRate()),
                convertRate(timer.getOneMinuteRate()),
                convertRate(timer.getFiveMinuteRate()),
                convertRate(timer.getFifteenMinuteRate()),
                getRateUnit(),
                getDurationUnit());
    }

    private void logMeter(final String name, final Meter meter) {
        loggerProxy.log(marker,
                "type=METER, name={}, count={}, mean_rate={}, m1={}, m5={}, m15={}, rate_unit={}",
                name,
                meter.getCount(),
                convertRate(meter.getMeanRate()),
                convertRate(meter.getOneMinuteRate()),
                convertRate(meter.getFiveMinuteRate()),
                convertRate(meter.getFifteenMinuteRate()),
                getRateUnit());
    }

    private void logHistogram(final String name, final Histogram histogram) {
        final Snapshot snapshot = histogram.getSnapshot();
        loggerProxy.log(marker,
                "type=HISTOGRAM, name={}, count={}, min={}, max={}, mean={}, stddev={}, " +
                        "median={}, p75={}, p95={}, p98={}, p99={}, p999={}",
                name,
                histogram.getCount(),
                snapshot.getMin(),
                snapshot.getMax(),
                snapshot.getMean(),
                snapshot.getStdDev(),
                snapshot.getMedian(),
                snapshot.get75thPercentile(),
                snapshot.get95thPercentile(),
                snapshot.get98thPercentile(),
                snapshot.get99thPercentile(),
                snapshot.get999thPercentile());
    }

    private void logCounter(final String name, final Counter counter) {
        loggerProxy.log(marker, "type=COUNTER, name={}, count={}", name, counter.getCount());
    }

    private void logGauge(final String name, final Gauge<?> gauge) {
        loggerProxy.log(marker, "type=GAUGE, name={}, value={}", name, gauge.getValue());
    }

    @Override
    protected String getRateUnit() {
        return "events/" + super.getRateUnit();
    }

    /* private class to allow logger configuration */
    static abstract class LoggerProxy {

        protected final Logger logger;

        public LoggerProxy(final Logger logger) {
            this.logger = logger;
        }

        abstract void log(final Marker marker, final String format, final Object... arguments);
    }

    /* private class to allow logger configuration */
    private static class DebugLoggerProxy extends LoggerProxy {

        public DebugLoggerProxy(final Logger logger) {
            super(logger);
        }

        @Override
        public void log(final Marker marker, final String format, final Object... arguments) {
            logger.debug(marker, format, arguments);
        }
    }

    /* private class to allow logger configuration */
    private static class TraceLoggerProxy extends LoggerProxy {

        public TraceLoggerProxy(final Logger logger) {
            super(logger);
        }

        @Override
        public void log(final Marker marker, final String format, final Object... arguments) {
            logger.trace(marker, format, arguments);
        }

    }

    /* private class to allow logger configuration */
    private static class InfoLoggerProxy extends LoggerProxy {

        public InfoLoggerProxy(final Logger logger) {
            super(logger);
        }

        @Override
        public void log(final Marker marker, final String format, final Object... arguments) {
            logger.info(marker, format, arguments);
        }
    }

    /* private class to allow logger configuration */
    private static class WarnLoggerProxy extends LoggerProxy {

        public WarnLoggerProxy(final Logger logger) {
            super(logger);
        }

        @Override
        public void log(final Marker marker, final String format, final Object... arguments) {
            logger.warn(marker, format, arguments);
        }
    }

    /* private class to allow logger configuration */
    private static class ErrorLoggerProxy extends LoggerProxy {

        public ErrorLoggerProxy(final Logger logger) {
            super(logger);
        }

        @Override
        public void log(final Marker marker, final String format, final Object... arguments) {
            logger.error(marker, format, arguments);
        }
    }

}
