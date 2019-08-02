package com.yanwu.spring.cloud.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The Exception utilities.
 */
public final class Exceptions {

    private Exceptions() {
    }

    public static RuntimeException unchecked(final Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new RuntimeException(e);
        }
    }

    public static String getStackTraceAsString(final Exception e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    @SuppressWarnings("unchecked")
    public static boolean isCausedBy(final Exception ex, final Class<? extends Exception>... causeExceptionClasses) {
        Throwable cause = ex.getCause();
        while (cause != null) {
            for (Class<? extends Exception> causeClass : causeExceptionClasses) {
                if (causeClass.isInstance(cause)) {
                    return true;
                }
            }
            cause = cause.getCause();
        }
        return false;
    }

}
