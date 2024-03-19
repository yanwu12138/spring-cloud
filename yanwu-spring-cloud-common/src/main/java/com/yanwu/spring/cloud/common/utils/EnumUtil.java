package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.enums.AbstractBaseEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Set;

/**
 * @author XuBaofeng.
 * @date 2024/3/13 12:00.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("all")
public class EnumUtil {

    private EnumUtil() {
        throw new UnsupportedOperationException("EnumUtil should never be instantiated");
    }

    public static <E extends AbstractBaseEnum> void checkEnumUniqueness(Class<E> clazz) {
        try {
            Boolean result = (Boolean) CommandUtil.invoke(clazz, "checkUniqueness", null);
            if (BooleanUtils.isFalse(result)) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            log.error("enum check uniqueness failed. class: {}", clazz.getName(), e);
            System.exit(-1);
        }
        log.info("enum check uniqueness success. class: {}", clazz.getName());
    }

    public static void main(String[] args) {
        Set<Class<? extends AbstractBaseEnum>> clazzSet = ObjectUtil.getClazz("com.yanwu.spring.cloud", AbstractBaseEnum.class);
        for (Class<? extends AbstractBaseEnum> clazz : clazzSet) {
            checkEnumUniqueness(clazz);
        }

    }

}
