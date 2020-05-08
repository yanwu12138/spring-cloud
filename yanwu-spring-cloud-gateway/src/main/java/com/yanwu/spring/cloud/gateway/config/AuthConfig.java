package com.yanwu.spring.cloud.gateway.config;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/8 15:55.
 * <p>
 * description:
 */
public class AuthConfig {

    /*** 接口白名单 */
    @Getter
    private static Set<String> passOperations;

    static {
        /* ----------------------------- 不登录调用接口 -----------------------------*/
        passOperations = new HashSet<>();
        passOperations.add("/base/webapp/login/login");
    }
}
