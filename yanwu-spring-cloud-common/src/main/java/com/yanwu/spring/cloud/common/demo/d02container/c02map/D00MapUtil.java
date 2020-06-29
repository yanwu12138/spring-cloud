package com.yanwu.spring.cloud.common.demo.d02container.c02map;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/19 15:03.
 * <p>
 * description:
 */
public class D00MapUtil {
    public static final Integer SIZE = 4;

    public static String str() {
        return RandomStringUtils.randomAlphabetic(4).toUpperCase();
    }

}
