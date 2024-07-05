package com.yanwu.spring.cloud.common.demo.d10design_mode.d01singleton;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2021-06-20 21:34:31.
 * <p>
 * describe: 单例模式
 * 饿汉式：类加载到内存后，就实例化一个对象，jvm保证线程安全
 */
@SuppressWarnings("all")
public class Singleton01 {
    private static final Singleton01 INSTANCE = new Singleton01();

    /**
     * 私有化构造方法
     */
    private Singleton01() {
    }

    /**
     * 向外提供对象的获取方法
     *
     * @return Singleton01对象
     */
    public static Singleton01 newInstance() {
        return INSTANCE;
    }

}
