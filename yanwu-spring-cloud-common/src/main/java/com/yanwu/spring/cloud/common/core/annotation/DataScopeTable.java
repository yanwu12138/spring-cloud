package com.yanwu.spring.cloud.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author XuBaofeng.
 * @date 2024/1/17 18:32.
 * <p>
 * description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DataScopeTable {

    /*** 表名 ***/
    String table();

    /*** 该表需要进行数据过滤的字段集合 ***/
    DataScopeField[] dataScope();

}
