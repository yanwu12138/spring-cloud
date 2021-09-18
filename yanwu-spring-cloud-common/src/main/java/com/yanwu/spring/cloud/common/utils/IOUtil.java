package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Baofeng Xu
 * @date 2020/11/24 9:23.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("all")
public class IOUtil {

    private IOUtil() {
        throw new UnsupportedOperationException("IOUtil should never be instantiated");
    }

    public static void close(AutoCloseable closeable) {
        if (Objects.isNull(closeable)) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            log.error("closeable close error.", e);
        }
    }

    public static void closes(AutoCloseable... closes) {
        if (ArrayUtils.isEmpty(closes)) {
            return;
        }
        for (AutoCloseable closeable : closes) {
            close(closeable);
        }
    }

    public static void close(Stream stream) {
        if (Objects.isNull(stream)) {
            return;
        }
        try {
            stream.close();
        } catch (Exception e) {
            log.error("stream close error.", e);
        }
    }

    public static void closes(Stream... closes) {
        if (ArrayUtils.isEmpty(closes)) {
            return;
        }
        for (Stream stream : closes) {
            close(stream);
        }
    }

}
