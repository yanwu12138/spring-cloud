package com.yanwu.spring.cloud.common.core.common;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/8 13:42.
 * <p>
 * description: 常量
 */
public interface Contents {
    String POINT = ".";
    String NUL = "";
    Integer DEFAULT_SIZE = 1024 * 10;
    /*** 用户登陆缓存：KEY ***/
    String LOGIN_TOKEN = "login_token_";
    /*** token ***/
    String TOKEN = "X-Token";
    /*** 每次请求的唯一标识 */
    String TX_ID = "txId";
    /*** 用户登陆缓存：有效期时长（单位：S） ***/
    Integer TOKEN_TIME_OUT = 60 * 60;
    /*** 日志输出 */
    String LOG_METHOD = "[Method]";
    String LOG_PARAM = "[Param]";
    String LOG_ERROR = "[Exception]";
}
