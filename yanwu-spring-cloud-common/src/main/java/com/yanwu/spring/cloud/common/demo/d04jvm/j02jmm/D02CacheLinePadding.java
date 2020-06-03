package com.yanwu.spring.cloud.common.demo.d04jvm.j02jmm;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-06-03 23:18:26.
 * <p>
 * describe:
 */
@SuppressWarnings("all")
public class D02CacheLinePadding {

    private static class Super {
        volatile long l1, l2, l3, l4, l5, l6, l7;
    }

    private static class Test extends Super {
        public volatile long x = 0L;
    }

    public static Test[] arr = new Test[2];

    static {
        arr[0] = new Test();
        arr[1] = new Test();
    }

    public static void main(String[] args) throws Exception {
        Thread thread1 = new Thread(() -> {
            for (long l = 0; l < 1000_0000L; l++) {
                arr[0].x = l;
            }
        });
        Thread thread2 = new Thread(() -> {
            for (long l = 0; l < 1000_0000L; l++) {
                arr[1].x = l;
            }
        });
        final long begin = System.nanoTime();
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(System.nanoTime() - begin);
    }
}
