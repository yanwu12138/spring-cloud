package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Closeable;
import java.util.Objects;

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

    public static void close(Closeable closeable) {
        if (Objects.isNull(closeable)) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            log.error("closeable close error.", e);
        }
    }

    public static void close(Closeable... closes) {
        if (ArrayUtils.isEmpty(closes)) {
            return;
        }
        for (Closeable closeable : closes) {
            close(closeable);
        }
    }

}
