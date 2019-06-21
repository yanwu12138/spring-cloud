package com.yanwu.spring.cloud.common.core.jmx;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * It is the sequence generator for notification about application health and component health
 */

@Component
public class JmxNotificationSequence {

    final private static AtomicLong sequence = new AtomicLong(0);

    public long next() {
        return sequence.incrementAndGet();
    }
}
