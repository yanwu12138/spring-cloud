package com.yanwu.spring.cloud.common.demo.d04jvm.reference;

import java.lang.ref.WeakReference;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 20:37:03.
 * <p>
 * describe:
 * 弱引用：如果该引用指向的对象没有被强引用指向，那么弱引用对象一遇到GC就会被骂上回收
 * * ThreadLocal
 */
public class D03WeakReference {
    public static void main(String[] args) throws Exception {
        WeakReference<Reference> reference = new WeakReference<>(new Reference());
        System.out.println(reference.get());
        System.gc();
        System.out.println(reference.get());
        System.in.read();
    }
}
