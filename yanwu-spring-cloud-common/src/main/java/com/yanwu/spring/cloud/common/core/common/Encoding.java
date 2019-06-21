package com.yanwu.spring.cloud.common.core.common;

import java.nio.charset.Charset;

public interface Encoding {
    String DEFAULT_STRING_ENCODING = "UTF-8";
    String DEFAULT_PACKAGE_ENCODING = "UTF-8";
    Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_STRING_ENCODING);
}
