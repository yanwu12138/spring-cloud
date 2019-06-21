package com.yanwu.spring.cloud.common.core.annotation;

import com.yanwu.spring.cloud.common.core.enums.CheckEnum;

import java.lang.annotation.*;

/**
 * @author XuBaofeng.
 * @date 2018-11-16 13:10.
 * <p>
 * description:
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@YanwuLog
public @interface CheckParam {

    CheckEnum check() default CheckEnum.DATA_NOT_NULL;

}
