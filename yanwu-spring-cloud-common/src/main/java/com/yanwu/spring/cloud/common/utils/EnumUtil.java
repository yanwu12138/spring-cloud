package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.lang.reflect.Method;

/**
 * @author XuBaofeng.
 * @date 2024/3/13 12:00.
 * <p>
 * description:
 */
@Slf4j
public class EnumUtil {

    private EnumUtil() {
        throw new UnsupportedOperationException("EnumUtil should never be instantiated");
    }

    public static void checkEnumUniqueness(Class<? extends Enum<?>> clazz) {
        try {
            Method method = clazz.getDeclaredMethod("checkUniqueness");
            method.setAccessible(true);
            Boolean result = (Boolean) method.invoke(null);
            if (BooleanUtils.isFalse(result)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            log.error("enum check uniqueness failed. class: {}", clazz.getName(), e);
            System.exit(-1);
        }
        log.info("enum check uniqueness success. class: {}", clazz.getName());
    }

}
