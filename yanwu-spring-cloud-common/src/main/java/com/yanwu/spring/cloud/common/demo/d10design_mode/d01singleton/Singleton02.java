package com.yanwu.spring.cloud.common.demo.d10design_mode.d01singleton;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2021-06-20 21:45:46.
 * <p>
 * describe: 懒汉式
 * 相对于饿汉式来讲，效率相对会比较低
 */
@SuppressWarnings("all")
public class Singleton02 {

    private static volatile Singleton02 INSTANCE;

    /**
     * 私有化构造
     */
    private Singleton02() {
    }

    /**
     * 向外提供对象的获取方法
     *
     * @return Singleton02对象
     * descreption: 使用双重检查可以避免线程不安全问题
     */
    public static Singleton02 getInstance() {
        if (INSTANCE == null) {
            synchronized (Singleton02.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Singleton02();
                }
            }
        }
        return INSTANCE;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> System.out.println(Singleton02.getInstance().hashCode())).start();
        }
    }
}
