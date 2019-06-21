package com.yanwu.spring.cloud.common.core.logging;

import org.slf4j.Logger;
import org.slf4j.event.Level;

/**
 * Customized Logger.
 */
public interface CustomLogger extends Logger {

    void log(final EventData eventData);

    void log(final Level logLevel, final EventData eventData);

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * ~
     *
     * The convenient methods for logging operation related information, such
     * as: operation name, status, duration, additional parameters, etc.
     *
     * The output is semi-construct message, and can be parsed by Logstash.
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     * ~
     */

    public enum OperationStatus {
        COMPLETED, SUCCESS, FAILURE
    }

    /**
     * Log start operation message without additional message.
     * <p>
     * The log level is {@code INFO}.
     *
     * <p>
     * The output message format:
     *
     * <pre>
     * [OPERATION] [START] operation name...
     * </pre>
     *
     * @param operationName operation name, shall be limited values, i.e. countable
     */
    void start(final String operationName);

    /**
     * Log start operation message with additional message.
     * <p>
     * The log level is {@code INFO}.
     *
     * <p>
     * The output message format:
     *
     * <pre>
     * [OPERATION] [START] operation name... {additional message}
     * </pre>
     *
     * @param operationName     operation name, shall be limited values, i.e. countable
     * @param additionalMessage the operation corresponding additional message, such as
     *                          context values
     */
    void start(final String operationName, final String additionalMessage);

    /**
     * Log stop operation message without additional message.
     * <p>
     * The log level is {@code INFO}.
     *
     * <p>
     * The default {@code OperationStatus} value is {@code COMPLETED}.
     *
     * <p>
     * The output message format:
     *
     * <pre>
     * [OPERATION] [COMPLETED] operation name. (duration ms)
     * </pre>
     *
     * @param operationName operation name, shall be limited values, i.e. countable
     * @param duration      operation duration, unit: milliseconds
     */
    void stop(final String operationName, final long duration);

    /**
     * Log stop operation message with additional message.
     * <p>
     * The log level is {@code INFO}.
     *
     * <p>
     * The default {@code OperationStatus} value is {@code COMPLETED}.
     *
     * <p>
     * The output message format:
     *
     * <pre>
     * [OPERATION] [COMPLETED] operation name. (duration ms) {additional message}
     * </pre>
     *
     * @param operationName operation name, shall be limited values, i.e. countable
     * @param duration      operation duration, unit: milliseconds
     */
    void stop(final String operationName, final long duration, final String additionalMessage);

    /**
     * Log stop operation message without additional message.
     * <p>
     * The log level is {@code INFO}.
     *
     * <p>
     * The output message format:
     *
     * <pre>
     * [OPERATION] [operation status] operation name. (duration ms)
     * </pre>
     *
     * @param operationName operation name, shall be limited values, i.e. countable
     * @param status        operation status
     * @param duration      operation duration, unit: milliseconds
     */
    void stop(final String operationName, final OperationStatus status, final long duration);

    /**
     * Log stop operation message without additional message.
     * <p>
     * The log level is {@code INFO}.
     *
     * <p>
     * The output message format:
     *
     * <pre>
     * [OPERATION] [operation status] operation name. (duration ms) {additional message}
     * </pre>
     *
     * @param operationName     operation name, shall be limited values, i.e. countable
     * @param status            operation status
     * @param duration          operation duration, unit: milliseconds
     * @param additionalMessage the operation corresponding additional message, such as
     *                          context values
     */
    void stop(final String operationName, final OperationStatus status, final long duration,
              final String additionalMessage);

}