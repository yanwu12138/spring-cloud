package com.yanwu.spring.cloud.common.data.datasource;

import java.lang.annotation.*;

/**
 * This is the annotation used to register an entity class as proper resource type
 * into {@HiveResourceRegistry} at runtime. By annotating an entity with
 *
 * @ResourceRegistrar(type=HiveResourceType.X), it will automatically register this
 * resource without explicitly doing it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ResourceRegistrar {
    HiveResourceType type() default HiveResourceType.SYSTEM_DB;
}
