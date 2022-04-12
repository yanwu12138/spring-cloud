package com.yanwu.spring.cloud.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author Baofeng Xu
 * @date 2022/4/12 10:36.
 * <p>
 * description: 默认10秒钟调用5次
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {

    /*** 秒数 ***/
    int seconds() default 10;

    /*** 单位时间内最多调用的接口次数 ***/
    int maxCount() default 5;

    /*** 访问该接口是否需要登录 ***/
    boolean needLogin() default true;

}
