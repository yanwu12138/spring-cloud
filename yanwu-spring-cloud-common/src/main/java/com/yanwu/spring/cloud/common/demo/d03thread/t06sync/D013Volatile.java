package com.yanwu.spring.cloud.common.demo.d03thread.t06sync;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 13:16.
 * <p>
 * description: volatile对于引用类型只能保证引用本身的可见性，并不能保证内部字段的可见性
 */
@SuppressWarnings("all")
public class D013Volatile {
    private static class Data {
        int a;
        int b;

        public Data(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    private static volatile Data data;

    public static void main(String[] args) {
        Thread write = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                data = new Data(i, i);
            }
        });
        Thread reader = new Thread(() -> {
            while (data == null) {
            }
            int x = data.a;
            int y = data.b;
            if (x != y) {
                System.out.printf("a: %s, b: %s%n", x, y);
            }
        });

        reader.start();
        write.start();

        try {
            reader.join();
            write.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("main end");
    }
}
