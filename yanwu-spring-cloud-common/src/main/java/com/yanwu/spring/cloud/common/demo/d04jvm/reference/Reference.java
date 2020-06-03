package com.yanwu.spring.cloud.common.demo.d04jvm.reference;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 19:19:43.
 * <p>
 * describe:
 */
public class Reference {

    @Override
    protected void finalize() throws Throwable {
        System.out.println("reference finalize...");
    }
}
