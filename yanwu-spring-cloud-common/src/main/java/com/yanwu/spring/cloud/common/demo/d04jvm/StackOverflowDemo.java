package com.yanwu.spring.cloud.common.demo.d04jvm;

/***
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * 模拟栈溢出异常a：线程请求的栈深度大于虚拟机所允许的最大深度
 * VM Options: -Xss128k
 */
public class StackOverflowDemo {
    private int stackLength = -1;

    private void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) {
        StackOverflowDemo demo = new StackOverflowDemo();
        try {
            demo.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length: " + demo.stackLength);
            throw e;
        }
    }
}
