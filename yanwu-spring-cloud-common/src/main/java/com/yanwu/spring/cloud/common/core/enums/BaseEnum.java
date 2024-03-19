package com.yanwu.spring.cloud.common.core.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XuBaofeng.
 * @date 2024/3/13 10:55.
 * <p>
 * description:
 */
public interface BaseEnum<K> {
    Logger log = LoggerFactory.getLogger(BaseEnum.class);

    <E extends BaseEnum<K>> E getInstance(K key);

    default boolean checkValidity(K key) {
        return getInstance(key) != null;
    }

}
