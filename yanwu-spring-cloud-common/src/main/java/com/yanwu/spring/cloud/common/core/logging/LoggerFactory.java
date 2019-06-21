package com.yanwu.spring.cloud.common.core.logging;

import org.slf4j.Logger;

/**
 * Customized LoggerFactory for code stack
 */
public final class LoggerFactory {

    private LoggerFactory() {
    }

    public static CustomLogger getLogger(final Class<?> clazz) {
        Logger logger = org.slf4j.LoggerFactory.getLogger(clazz);
        return new CustomLoggerImpl(logger);
    }

    public static CustomLogger getLogger(final String name) {
        Logger logger = org.slf4j.LoggerFactory.getLogger(name);
        return new CustomLoggerImpl(logger);
    }

}
