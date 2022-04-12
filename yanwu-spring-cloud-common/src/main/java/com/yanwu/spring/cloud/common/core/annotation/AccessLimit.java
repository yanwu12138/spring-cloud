package com.yanwu.spring.cloud.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author Baofeng Xu
 * @date 2022/4/12 10:36.
 * <p>
 * description: 默认一秒钟调用一次
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimit {

    /*** 秒数 ***/
    int seconds() default 1;

    /*** 单位时间内最多调用的接口次数 ***/
    int maxCount() default 1;

    /*** 访问该接口是否需要登录 ***/
    boolean needLogin() default true;

}
