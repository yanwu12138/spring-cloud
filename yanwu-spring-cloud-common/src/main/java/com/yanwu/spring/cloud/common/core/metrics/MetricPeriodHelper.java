package com.yanwu.spring.cloud.common.core.metrics;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The helper class for metric period.
 */
public final class MetricPeriodHelper {

    public static final int DAYS_WEEK = 7;

    public static final long SECONDS_WEEK = TimeUnit.DAYS.toSeconds(DAYS_WEEK);
    public static final long SECONDS_DAY = TimeUnit.DAYS.toSeconds(1);
    public static final long SECONDS_HOUR = TimeUnit.HOURS.toSeconds(1);
    public static final long SECONDS_MINUTE = TimeUnit.MINUTES.toSeconds(1);

    private MetricPeriodHelper() {
    }

    public static void validatePositive(final int value) {
        checkArgument(value > 0, "Not a positive integer number: " + value);
    }

    public static long toValidateSeconds(final long time, final TimeUnit unit) {
        long seconds = unit.toSeconds(time);
        if (seconds < 1) {
            throw new IllegalArgumentException("The minimal period is ONE SECONDS");
        }
        return seconds;
    }

    /**
     * Convert the time to period tag, minimal period is ONE SECONDS.
     * <p>
     * Because there are no {@code WEEKS}, for convenient any time that exactly match the WEEK period (multiple of 7
     * DAYS) will convert to corresponding week tag; Because the month days is not fixed (28-31 DAYS), seldom used in
     * the application-level metrics, and the tag conflicts with {@MINITES}, I decide to not support
     * {@code MONTHS} tag.
     * <p>
     * Examples:
     * <ul>
     * <li>7 seconds -> 7s
     * <li>30 seconds -> 1m
     * <li>1 minute -> 1m
     * <li>3 days -> 3d
     * <li>7 days -> 1w
     * <li>13 days -> 13d
     * <li>14 days -> 2w
     * <li>30 days -> 30d
     * <li>35 days -> 5w
     * <li>70 days -> 10w
     *
     * @param time time value
     * @param unit time unit
     * @return reasonable period tag
     * @throws IllegalArgumentException if time period is less than ONE SECONDS
     */
    public static String toPeriodTag(final long time, final TimeUnit unit) {
        long seconds = toValidateSeconds(time, unit);

        if (seconds % SECONDS_WEEK == 0) {
            return seconds / SECONDS_WEEK + "w";
        }

        if (seconds % SECONDS_DAY == 0) {
            return seconds / SECONDS_DAY + "d";
        }

        if (seconds % SECONDS_HOUR == 0) {
            return seconds / SECONDS_HOUR + "h";
        }

        if (seconds % SECONDS_MINUTE == 0) {
            return seconds / SECONDS_MINUTE + "m";
        }

        return String.valueOf(time) + 's';
    }

}
