package com.yanwu.spring.cloud.common.utils;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * The utilities to process resource.
 */
public final class Resources {

    private Resources() {
    }

    /**
     * Get the inputstream from file system, system classpath and context resource in sequence, return immediately if
     * found.
     *
     * @param resourceName file name or resource name
     * @return the resource input stream
     * @throws IllegalArgumentException if cannot found the resource
     */
    public static InputStream getInputStream(final String resourceName) {
        try {
            return new FileInputStream(resourceName);
        } catch (FileNotFoundException e1) {
            // ignore
        }

        InputStream is = ClassLoader.getSystemResourceAsStream(resourceName);
        if (is == null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }

        if (is == null) {
            throw new IllegalArgumentException("Cannot load [" + resourceName
                    + "] from any place, please check the name and the environment.");
        }

        return is;
    }

    /**
     * Get resource content as text.
     *
     * @param resourceName file name or resource name
     * @return the text content of the resource
     */
    public static String getText(final String resourceName) {
        try {
            return IOUtils.toString(getInputStream(resourceName));
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

}
