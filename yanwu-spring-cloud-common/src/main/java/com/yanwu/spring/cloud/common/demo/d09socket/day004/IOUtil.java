package com.yanwu.spring.cloud.common.demo.d09socket.day004;

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
public class IOUtil {

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

    public static void close(Closeable... closeables) {
        if (ArrayUtils.isEmpty(closeables)) {
            return;
        }
        for (Closeable closeable : closeables) {
            close(closeable);
        }
    }

}
