package com.yanwu.spring.cloud.common.demo.d04jvm.reference;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 19:36:01.
 * <p>
 * describe:
 * 软引用：当即将出现内存溢出时回收，如果这次回收后还没有足够的内存，才会抛出内存溢出异常
 * -Xmx20M -Xms20M
 */
public class D02SoftReference {
    // ----- 1M
    private static final Integer SIZE = 1024 * 1024;

    public static void main(String[] args) throws Exception {
        SoftReference<byte[]> reference = new SoftReference<>(new byte[SIZE * 10]);
        System.out.println(reference.get());
        System.gc();
        // ----- 此时内存足够，gc()不会回收软引用的对象
        TimeUnit.MILLISECONDS.sleep(1000);
        System.out.println(reference.get());
        // ----- 再分配一个数组，heap即将装不下，先回收一次，如果不够，会将软引用回收
        byte[] bytes = new byte[SIZE * 15];
        // ----- 内存最大20M，此时软引用将会被回收掉
        System.out.println(reference.get());
    }

}
