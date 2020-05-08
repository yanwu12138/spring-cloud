package com.yanwu.spring.cloud.common.core.common;

import java.nio.charset.Charset;

public interface Encoding {
    String UTF_8 = "UTF-8";
    Charset DEFAULT_CHARSET = Charset.forName(UTF_8);
}
