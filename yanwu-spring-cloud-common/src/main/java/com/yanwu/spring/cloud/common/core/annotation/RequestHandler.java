package com.yanwu.spring.cloud.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author XuBaofeng.
 * @date 2018-11-07 13:47.
 * <p>
 * description: 输出方法的入参、出参
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestHandler {

    String value() default "服务器异常";

    /*** 是否过滤role或user数据权限，默认为不过滤 ***/
    UserAccesses dataScope() default @UserAccesses(shop = false, agent = false);

}
