package com.yanwu.spring.cloud.common.data.entity;

import java.io.Serializable;

public interface BaseObject extends Serializable, Cloneable {
    short DEFAULT_SHORT_STRING_LENGTH   = 16;
    short DEFAULT_STRING_LENGTH         = 32;
    short DEFAULT_LONG_STRING_LENGTH    = 64;
    short DEFAULT_DOUBLE_STRING_LENGTH  = 128;
    short DEFAULT_FILENAME_LENGTH       = 256;
    short DEFAULT_URL_LENGTH            = 256;
    short DEFAULT_DESCRIPTION_LENGTH    = 1024;
    short DEFAULT_LARGE_STRING_LENGTH   = 2048;
}
