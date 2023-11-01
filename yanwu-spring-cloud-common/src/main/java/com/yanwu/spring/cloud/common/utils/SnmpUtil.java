package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author XuBaofeng.
 * @date 2023/10/8 09:55.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class SnmpUtil {

    private SnmpUtil() {
        throw new UnsupportedOperationException("MIBUtil should never be instantiated");
    }

    public static void main(String[] args) {
        System.out.println(RandomStringUtils.randomAlphanumeric(32).toLowerCase());
    }

}
