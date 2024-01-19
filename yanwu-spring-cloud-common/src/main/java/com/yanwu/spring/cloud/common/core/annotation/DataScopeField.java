package com.yanwu.spring.cloud.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author XuBaofeng.
 * @date 2024/1/17 18:34.
 * <p>
 * description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface DataScopeField {

    /*** 需要进行数据过滤的字段名称 ***/
    String field();

    /*** 数据过滤类型【一级代理商 || 店铺】 ***/
    long[] dates() default {};

}
