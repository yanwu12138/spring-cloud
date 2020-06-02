package com.yanwu.spring.cloud.common.demo.d04jvm;

/***
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 *
 * description:
 */
@SuppressWarnings("all")
public class StackOutOfMemoryDemo {
    public static void main(String[] args) {
        stackLeakByThread();
    }

    private static void stackLeakByThread() {
        for (; ; ) {
            new Thread(() -> {
                for (; ; ) {
                }
            }).start();
        }
    }
}
