package com.yanwu.spring.cloud.common.demo.d04jvm.reference;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 19:19:26.
 * <p>
 * describe:
 * 强引用：只要有强引用指向对象，那么即时出现OOM也不会回收该对象
 */
public class D01NormalReference {

    public static void main(String[] args) throws Exception {
        Reference reference = new Reference();
        reference = null;
        System.gc();
        TimeUnit.HOURS.sleep(1);
    }

}
