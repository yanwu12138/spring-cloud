package com.yanwu.spring.cloud.common.core.logging;

import java.lang.annotation.*;

/**
 * Custom @Logger annotation
 **/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Loggable {

}
