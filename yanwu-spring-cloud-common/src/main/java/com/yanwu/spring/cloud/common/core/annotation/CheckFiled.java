package com.yanwu.spring.cloud.common.core.annotation;

import java.lang.annotation.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-02 13:56.
 * <p>
 * description:
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckFiled {

    /*** 参数字段 ***/
    String field();

    /*** 正则表达式 */
    String regex();

    /*** 当校验不合法时的返回提示信息 */
    String message();

}
