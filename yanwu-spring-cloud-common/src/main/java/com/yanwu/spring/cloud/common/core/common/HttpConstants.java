package com.yanwu.spring.cloud.common.core.common;

import org.apache.commons.lang3.RandomStringUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Baofeng Xu
 * @date 2020/11/24 10:07.
 * <p>
 * description:
 */
public interface HttpConstants {

    String KEY_VALUE_SP = "=";
    String PARAMS_SP = "&";
    String QUERY_SP = "?";
    Charset UTF8 = StandardCharsets.UTF_8;

    String ACCEPT = "Accept";
    String CONTENT_TYPE = "Content-type";
    String APPLICATION_JSON = "application/json; charset=utf-8";
}
