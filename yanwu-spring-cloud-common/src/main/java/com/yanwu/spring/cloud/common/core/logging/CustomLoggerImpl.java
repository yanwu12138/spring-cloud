package com.yanwu.spring.cloud.common.core.logging;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * The implementation of customized Logger.
 */
public class CustomLoggerImpl implements CustomLogger {

    private static final String FQCN = CustomLoggerImpl.class.getName();
    private final Logger logger;

    CustomLoggerImpl(final Logger logger) {
        this.logger = logger;
    }

    protected Logger getLogger() {
        return logger;
    }

    @Override
    public String getName() {
        return logger.getName();
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * private implementation
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private boolean isLevelEnabled(final int level) {
        switch (level) {
            case LocationAwareLogger.TRACE_INT:
                return logger.isTraceEnabled();
            case LocationAwareLogger.DEBUG_INT:
                return logger.isDebugEnabled();
            case LocationAwareLogger.INFO_INT:
                return logger.isInfoEnabled();
            case LocationAwareLogger.WARN_INT:
                return logger.isWarnEnabled();
            case LocationAwareLogger.ERROR_INT:
                return logger.isErrorEnabled();
            default:
                return false;
        }
    }

    private void log(final int level, final String msg) {
        log(null, level, msg);
    }

    private void log(final int level, final String format, final Object arg) {
        log(null, level, format, arg);
    }

    private void log(final int level, final String format, final Object arg1, final Object arg2) {
        log(null, level, format, arg1, arg2);
    }

    private void log(final int level, final String format, final Object... arguments) {
        log(null, level, format, arguments);
    }

    private void log(final int level, final String msg, final Throwable t) {
        log(null, level, msg, t);
    }

    private void log(final Marker marker, final int level, final String msg) {
        if (!isLevelEnabled(level)) {
            return;
        }
        if (logger instanceof LocationAwareLogger) {
            ((LocationAwareLogger) logger).log(marker, FQCN, level, msg, null, null);
        } else {
            switch (level) {
                case LocationAwareLogger.TRACE_INT:
                    logger.trace(marker, msg);
                    break;
                case LocationAwareLogger.DEBUG_INT:
                    logger.debug(marker, msg);
                    break;
                case LocationAwareLogger.INFO_INT:
                    logger.info(marker, msg);
                    break;
                case LocationAwareLogger.WARN_INT:
                    logger.warn(marker, msg);
                    break;
                case LocationAwareLogger.ERROR_INT:
                    logger.error(marker, msg);
                    break;
                default:
                    break;
            }
        }
    }

    private void log(final Marker marker, final int level, final String format, final Object arg) {
        if (!isLevelEnabled(level)) {
            return;
        }

        if (logger instanceof LocationAwareLogger) {
            String formattedMessage = MessageFormatter.format(format, arg).getMessage();
            ((LocationAwareLogger) logger).log(marker, FQCN, level, formattedMessage, new Object[]{arg}, null);
        } else {
            switch (level) {
                case LocationAwareLogger.TRACE_INT:
                    logger.trace(marker, format, arg);
                    break;
                case LocationAwareLogger.DEBUG_INT:
                    logger.debug(marker, format, arg);
                    break;
                case LocationAwareLogger.INFO_INT:
                    logger.info(marker, format, arg);
                    break;
                case LocationAwareLogger.WARN_INT:
                    logger.warn(marker, format, arg);
                    break;
                case LocationAwareLogger.ERROR_INT:
                    logger.error(marker, format, arg);
                    break;
                default:
                    break;
            }
        }

    }

    private void log(final Marker marker, final int level, final String format, final Object arg1, final Object arg2) {
        if (!isLevelEnabled(level)) {
            return;
        }

        if (logger instanceof LocationAwareLogger) {
            LocationAwareLogger lal = (LocationAwareLogger) logger;
            if (arg2 instanceof Throwable) {
                String formattedMessage = MessageFormatter.format(format, arg1).getMessage();
                lal.log(marker, FQCN, level, formattedMessage, new Object[]{arg1}, (Throwable) arg2);
            } else {
                String formattedMessage = MessageFormatter.format(format, arg1, arg2).getMessage();
                lal.log(marker, FQCN, level, formattedMessage, new Object[]{arg1, arg2}, null);
            }
        } else {
            switch (level) {
                case LocationAwareLogger.TRACE_INT:
                    logger.trace(marker, format, arg1, arg2);
                    break;
                case LocationAwareLogger.DEBUG_INT:
                    logger.debug(marker, format, arg1, arg2);
                    break;
                case LocationAwareLogger.INFO_INT:
                    logger.info(marker, format, arg1, arg2);
                    break;
                case LocationAwareLogger.WARN_INT:
                    logger.warn(marker, format, arg1, arg2);
                    break;
                case LocationAwareLogger.ERROR_INT:
                    logger.error(marker, format, arg1, arg2);
                    break;
                default:
                    break;
            }
        }
    }

    private void log(final Marker marker, final int level, final String format, final Object... arguments) {
        if (!isLevelEnabled(level)) {
            return;
        }

        if (logger instanceof LocationAwareLogger) {
            LocationAwareLogger lal = (LocationAwareLogger) logger;
            Object lastArg = arguments[arguments.length - 1];
            if (lastArg instanceof Throwable) {
                Object[] args = new Object[arguments.length - 1];
                System.arraycopy(arguments, 0, args, 0, arguments.length - 1);
                String formattedMessage = MessageFormatter.arrayFormat(format, args).getMessage();
                lal.log(marker, FQCN, level, formattedMessage, args, (Throwable) lastArg);
            } else {
                String formattedMessage = MessageFormatter.arrayFormat(format, arguments).getMessage();
                lal.log(marker, FQCN, level, formattedMessage, arguments, null);
            }
        } else {
            switch (level) {
                case LocationAwareLogger.TRACE_INT:
                    logger.trace(marker, format, arguments);
                    break;
                case LocationAwareLogger.DEBUG_INT:
                    logger.debug(marker, format, arguments);
                    break;
                case LocationAwareLogger.INFO_INT:
                    logger.info(marker, format, arguments);
                    break;
                case LocationAwareLogger.WARN_INT:
                    logger.warn(marker, format, arguments);
                    break;
                case LocationAwareLogger.ERROR_INT:
                    logger.error(marker, format, arguments);
                    break;
                default:
                    break;
            }
        }

    }

    private void log(final Marker marker, final int level, final String msg, final Throwable t) {
        if (!isLevelEnabled(level)) {
            return;
        }

        if (logger instanceof LocationAwareLogger) {
            ((LocationAwareLogger) logger).log(marker, FQCN, level, msg, null, t);
        } else {
            switch (level) {
                case LocationAwareLogger.TRACE_INT:
                    logger.trace(marker, msg, t);
                    break;
                case LocationAwareLogger.DEBUG_INT:
                    logger.debug(marker, msg, t);
                    break;
                case LocationAwareLogger.INFO_INT:
                    logger.info(marker, msg, t);
                    break;
                case LocationAwareLogger.WARN_INT:
                    logger.warn(marker, msg, t);
                    break;
                case LocationAwareLogger.ERROR_INT:
                    logger.error(marker, msg, t);
                    break;
                default:
                    break;
            }
        }
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Delegate to private implementations
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    @Override
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    @Override
    public void trace(final String msg) {
        log(LocationAwareLogger.TRACE_INT, msg);
    }

    @Override
    public void trace(final String format, final Object arg) {
        log(LocationAwareLogger.TRACE_INT, format, arg);
    }

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        log(LocationAwareLogger.TRACE_INT, format, arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object... arguments) {
        log(LocationAwareLogger.TRACE_INT, format, arguments);
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        log(LocationAwareLogger.TRACE_INT, msg, t);
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public void trace(final Marker marker, final String msg) {
        log(marker, LocationAwareLogger.TRACE_INT, msg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        log(marker, LocationAwareLogger.TRACE_INT, format, arg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(marker, LocationAwareLogger.TRACE_INT, format, arg1, arg2);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object... arguments) {
        log(marker, LocationAwareLogger.TRACE_INT, format, arguments);
    }

    @Override
    public void trace(final Marker marker, final String msg, final Throwable t) {
        log(marker, LocationAwareLogger.TRACE_INT, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(final String msg) {
        log(LocationAwareLogger.DEBUG_INT, msg);
    }

    @Override
    public void debug(final String format, final Object arg) {
        log(LocationAwareLogger.DEBUG_INT, format, arg);
    }

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        log(LocationAwareLogger.DEBUG_INT, format, arg1, arg2);
    }

    @Override
    public void debug(final String format, final Object... arguments) {
        log(LocationAwareLogger.DEBUG_INT, format, arguments);
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        log(LocationAwareLogger.DEBUG_INT, msg, t);
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(final Marker marker, final String msg) {
        log(marker, LocationAwareLogger.DEBUG_INT, msg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        log(marker, LocationAwareLogger.DEBUG_INT, format, arg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(marker, LocationAwareLogger.DEBUG_INT, format, arg1, arg2);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object... arguments) {
        log(marker, LocationAwareLogger.DEBUG_INT, format, arguments);
    }

    @Override
    public void debug(final Marker marker, final String msg, final Throwable t) {
        log(marker, LocationAwareLogger.DEBUG_INT, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(final String msg) {
        log(LocationAwareLogger.INFO_INT, msg);
    }

    @Override
    public void info(final String format, final Object arg) {
        log(LocationAwareLogger.INFO_INT, format, arg);
    }

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        log(LocationAwareLogger.INFO_INT, format, arg1, arg2);
    }

    @Override
    public void info(final String format, final Object... arguments) {
        log(LocationAwareLogger.INFO_INT, format, arguments);
    }

    @Override
    public void info(final String msg, final Throwable t) {
        log(LocationAwareLogger.INFO_INT, msg, t);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(final Marker marker, final String msg) {
        log(marker, LocationAwareLogger.INFO_INT, msg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        log(marker, LocationAwareLogger.INFO_INT, format, arg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(marker, LocationAwareLogger.INFO_INT, format, arg1, arg2);
    }

    @Override
    public void info(final Marker marker, final String format, final Object... arguments) {
        log(marker, LocationAwareLogger.INFO_INT, format, arguments);
    }

    @Override
    public void info(final Marker marker, final String msg, final Throwable t) {
        log(marker, LocationAwareLogger.INFO_INT, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(final String msg) {
        log(LocationAwareLogger.WARN_INT, msg);
    }

    @Override
    public void warn(final String format, final Object arg) {
        log(LocationAwareLogger.WARN_INT, format, arg);
    }

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        log(LocationAwareLogger.WARN_INT, format, arg1, arg2);
    }

    @Override
    public void warn(final String format, final Object... arguments) {
        log(LocationAwareLogger.WARN_INT, format, arguments);
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        log(LocationAwareLogger.WARN_INT, msg, t);
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(final Marker marker, final String msg) {
        log(marker, LocationAwareLogger.WARN_INT, msg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        log(marker, LocationAwareLogger.WARN_INT, format, arg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(marker, LocationAwareLogger.WARN_INT, format, arg1, arg2);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object... arguments) {
        log(marker, LocationAwareLogger.WARN_INT, format, arguments);
    }

    @Override
    public void warn(final Marker marker, final String msg, final Throwable t) {
        log(marker, LocationAwareLogger.WARN_INT, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(final String msg) {
        log(LocationAwareLogger.ERROR_INT, msg);
    }

    @Override
    public void error(final String format, final Object arg) {
        log(LocationAwareLogger.ERROR_INT, format, arg);
    }

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        log(LocationAwareLogger.ERROR_INT, format, arg1, arg2);
    }

    @Override
    public void error(final String format, final Object... arguments) {
        log(LocationAwareLogger.ERROR_INT, format, arguments);
    }

    @Override
    public void error(final String msg, final Throwable t) {
        log(LocationAwareLogger.ERROR_INT, msg, t);
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(final Marker marker, final String msg) {
        log(marker, LocationAwareLogger.ERROR_INT, msg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        log(marker, LocationAwareLogger.ERROR_INT, format, arg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        log(marker, LocationAwareLogger.ERROR_INT, format, arg1, arg2);
    }

    @Override
    public void error(final Marker marker, final String format, final Object... arguments) {
        log(marker, LocationAwareLogger.ERROR_INT, format, arguments);
    }

    @Override
    public void error(final Marker marker, final String msg, final Throwable t) {
        log(marker, LocationAwareLogger.ERROR_INT, msg, t);
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * EventData and TransactionData logging
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    @Override
    public void log(final EventData eventData) {
        EventLogger.logEvent(this, eventData);
    }

    @Override
    public void log(final Level logLevel, final EventData eventData) {
        EventLogger.logEvent(logLevel, this, eventData);
    }

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Operation logging
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private static final String CATEGORY_OPERATION = "OPERATION";

    @Override
    public void start(final String operationName) {
        this.info("[{}] [START] {}...", CATEGORY_OPERATION, operationName);
    }

    @Override
    public void start(final String operationName, final String additionalMessage) {
        this.info("[{}] [START] {}... {{}}", CATEGORY_OPERATION, operationName, additionalMessage);
    }

    @Override
    public void stop(final String operationName, final long duration) {
        this.stop(operationName, OperationStatus.COMPLETED, duration);
    }

    @Override
    public void stop(final String operationName, final long duration, final String additionalMessage) {
        this.stop(operationName, OperationStatus.COMPLETED, duration, additionalMessage);
    }

    @Override
    public void stop(final String operationName, final OperationStatus status, final long duration) {
        this.info("[{}] [{}] {}. ({} ms)", CATEGORY_OPERATION, status, operationName, duration);
    }

    @Override
    public void stop(final String operationName, final OperationStatus status, final long duration,
                     final String additionalMessage) {
        this.info("[{}] [{}] {}. ({} ms) {{}}", CATEGORY_OPERATION, status, operationName, duration, additionalMessage);
    }

}