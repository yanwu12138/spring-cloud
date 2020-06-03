package com.yanwu.spring.cloud.common.demo.d03thread.t06sync;

import sun.misc.Unsafe;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 14:57.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class D031Unsafe {
    private static class Test {
        private Test() {
        }

        int i = 0;
    }

    public static void main(String[] args) throws Exception {
        Unsafe unsafe = Unsafe.getUnsafe();
        Test test = (Test) unsafe.allocateInstance(Test.class);
        test.i = 9;
        System.out.println(test.i);
    }
}
