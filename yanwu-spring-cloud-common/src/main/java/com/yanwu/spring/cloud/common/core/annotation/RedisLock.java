package com.yanwu.spring.cloud.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author XuBaofeng.
 * @date 2023/9/14 20:34.
 * <p>
 * description:
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

    /*** redis锁的key: spel表达式 ***/
    String suffix();

    /*** 是否根据{类路径+方法}名来上锁, 从未锁定整个方法
     * 当根据{类路径+方法}来上锁时，不校验suffix属性
     * ***/
    boolean lockMethod() default false;

    /***
     * redis锁的加锁时间，单位：秒，该值未-1时，时长为默认时长
     */
    int lockTime() default -1;

}
