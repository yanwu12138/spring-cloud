package com.yanwu.spring.cloud.common.demo.d04jvm.gc;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/25 16:26.
 * <p>
 * description: 引用计数算法
 */
public class ReferenceCountingAlgorithm {

    public Object instance;
    private static final int MB = 1024 * 1024;

    /***
     * 该成员属性的唯一意义就是占有内存，方便在GC日志中看清楚是否被回收
     */
    private byte[] bytes = new byte[2 * MB];

    public static void main(String[] args) {
        ReferenceCountingAlgorithm objA = new ReferenceCountingAlgorithm();
        ReferenceCountingAlgorithm objB = new ReferenceCountingAlgorithm();
        objA.instance = objB;
        objB.instance = objA;

        objA = null;
        objB = null;

        System.gc();
    }
}
