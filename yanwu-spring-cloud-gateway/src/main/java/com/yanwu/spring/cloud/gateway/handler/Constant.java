package com.yanwu.spring.cloud.gateway.handler;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/2 17:52.
 * <p>
 * description:
 */
public class Constant {
    /*** 每次请求的唯一标识 ***/
    public static final String TX_ID = "txId";
    /*** 请求头中的用户信息 ***/
    public static final String CURRENT_USER = "currentUser";
    /*** 请求头中的Token信息 ***/
    public static final String TOKEN = "X-Token";
    /*** 日志输出 */
    static final String LOG_METHOD = "[Method]";
    static final String LOG_PARAM = "[Param]";
    static final String LOG_ERROR = "[Exception]";
}
