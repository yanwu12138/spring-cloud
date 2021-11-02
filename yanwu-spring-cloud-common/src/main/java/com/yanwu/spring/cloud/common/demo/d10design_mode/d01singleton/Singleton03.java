package com.yanwu.spring.cloud.common.demo.d10design_mode.d01singleton;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2021-06-20 22:05:12.
 * <p>
 * describe: 饿汉式：静态内部类
 */
@SuppressWarnings("all")
public class Singleton03 {

    /**
     * 私有化构造
     */
    private Singleton03() {
    }

    /**
     * 向外提供对象的获取方法
     *
     * @return Singleton03对象
     */
    public static Singleton03 getInstance() {
        return Singleton03Holder.INSTANCE;
    }

    /**
     * Singleton03对象的持有者
     */
    private static class Singleton03Holder {
        private static final Singleton03 INSTANCE = new Singleton03();
    }

}
