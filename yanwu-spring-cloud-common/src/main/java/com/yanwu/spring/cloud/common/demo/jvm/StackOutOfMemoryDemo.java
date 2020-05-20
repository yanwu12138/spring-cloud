package com.yanwu.spring.cloud.common.demo.jvm;

/***
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 *
 * description:
 */
public class StackOutOfMemoryDemo {
    public static void main(String[] args) {
        stackLeakByThread();
    }

    private static void stackLeakByThread() {
        while (true) {
            new Thread(() -> {
                while (true) {
                }
            }).start();
        }
    }
}
