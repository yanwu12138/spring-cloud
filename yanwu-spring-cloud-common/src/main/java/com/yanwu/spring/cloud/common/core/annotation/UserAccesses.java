package com.yanwu.spring.cloud.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author XuBaofeng.
 * @date 2024/1/19 15:34.
 * <p>
 * description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface UserAccesses {

    /*** 是否过滤role数据权限，默认为过滤 ***/
    boolean agent() default true;

    /*** 是否过滤user数据权限，默认为过滤 ***/
    boolean shop() default true;

}
