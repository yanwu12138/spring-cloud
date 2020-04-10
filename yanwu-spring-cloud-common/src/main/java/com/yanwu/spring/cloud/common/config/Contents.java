package com.yanwu.spring.cloud.common.config;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020-01-14 17:42.
 * <p>
 * description:
 */
public class Contents {
    /*** 用户登陆缓存：KEY ***/
    public static final String LOGIN_TOKEN = "login_token_";
    /*** 每次请求的唯一标识 */
    public static final String TX_ID = "txId";
    /*** 用户登陆缓存：有效期时长（单位：S） ***/
    public static final Integer TOKEN_TIME_OUT = 60 * 60;


}
