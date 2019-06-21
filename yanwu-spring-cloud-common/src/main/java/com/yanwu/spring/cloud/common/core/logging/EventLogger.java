package com.yanwu.spring.cloud.common.core.logging;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.event.Level;

class EventLogger {

    static Marker EVENT_MARKER = MarkerFactory.getMarker("EVENT");

    private EventLogger() {
    }

    public static void logEvent(CustomLogger logger, EventData data) {
        logger.debug(EVENT_MARKER, data.toString());
    }

    public static void logEvent(Level logLevel, CustomLogger logger, EventData data) {
        if (Level.INFO.equals(logLevel)) {
            logger.info(EVENT_MARKER, data.toString());
        } else if (Level.DEBUG.equals(logLevel)) {
            logger.debug(EVENT_MARKER, data.toString());
        } else if (Level.WARN.equals(logLevel)) {
            logger.warn(EVENT_MARKER, data.toString());
        } else if (Level.ERROR.equals(logLevel)) {
            logger.error(EVENT_MARKER, data.toString());
        }
    }

}