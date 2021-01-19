package com.yanwu.spring.cloud.common.core.annotation;

import com.yanwu.spring.cloud.common.core.common.Contents;
import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * @author Baofeng Xu
 * @date 2021/1/19 14:30.
 * <p>
 * description: 接口版本控制
 */
@Mapping
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ApiVersion {

    String value() default Contents.DEFAULT_VERSION;

}
