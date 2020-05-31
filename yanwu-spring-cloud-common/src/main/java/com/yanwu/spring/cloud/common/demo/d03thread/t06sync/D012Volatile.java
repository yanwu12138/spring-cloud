package com.yanwu.spring.cloud.common.demo.d03thread.t06sync;

import java.util.concurrent.TimeUnit;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:05.
 * <p>
 * description: volatile对于引用类型只能保证引用本身的可见性，并不能保证内部字段的可见性
 */
@SuppressWarnings("all")
public class D012Volatile {

    private Boolean flag = true;
    private static volatile D012Volatile s = new D012Volatile();

    public static void main(String[] args) {
        new Thread(s::test).start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        s.flag = true;
    }

    private void test() {
        System.out.println("test start");
        while (flag) {
            System.out.println("test running");
        }
        System.out.println("test end");
    }
}
