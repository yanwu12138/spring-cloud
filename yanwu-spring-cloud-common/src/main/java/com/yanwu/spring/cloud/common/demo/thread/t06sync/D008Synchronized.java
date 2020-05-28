package com.yanwu.spring.cloud.common.demo.thread.t06sync;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:55.
 * <p>
 * description: synchronized具有可重入性
 */
@SuppressWarnings("all")
public class D008Synchronized {


    public static void main(String[] args) {
        new D008Synchronized().test1();
    }

    public synchronized void test1() {
        System.out.println("test1 start");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        test2();
        System.out.println("test1 end");
    }

    public synchronized void test2() {
        System.out.println("test2 start");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("test2 end");
    }

}
