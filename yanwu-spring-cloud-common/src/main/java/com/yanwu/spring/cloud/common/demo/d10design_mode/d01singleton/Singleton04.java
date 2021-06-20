package com.yanwu.spring.cloud.common.demo.d10design_mode.d01singleton;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2021-06-20 22:08:56.
 * <p>
 * describe: 不仅可以解决线程同步，还可以防止被反射的方式获取对象
 */
@SuppressWarnings("all")
public enum Singleton04 {

    INSTANCE;

    public int sout(int i) {
        return i;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            new Thread(() -> {
                Singleton04 singleton04 = Singleton04.INSTANCE;
                System.out.println(singleton04.sout(finalI) + ": " + singleton04.hashCode());
            }).start();
        }
    }
}
