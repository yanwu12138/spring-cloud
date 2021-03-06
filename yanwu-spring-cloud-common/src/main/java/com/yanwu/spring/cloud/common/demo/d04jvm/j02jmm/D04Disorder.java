package com.yanwu.spring.cloud.common.demo.d04jvm.j02jmm;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-06-03 23:28:28.
 * <p>
 * describe: 指令重排证明
 */
@SuppressWarnings("all")
public class D04Disorder {
    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        for (; ; ) {
            i++;
            x = 0;
            y = 0;
            a = 0;
            b = 0;
            Thread one = new Thread(() -> {
                //由于线程one先启动，下面这句话让它等一等线程two. 读着可根据自己电脑的实际性能适当调整等待时间.
                //shortWait(100000);
                a = 1;
                x = b;
            });
            Thread two = new Thread(() -> {
                b = 1;
                y = a;
            });
            one.start();
            two.start();
            one.join();
            two.join();
            if (x == 0 && y == 0) {
                // ----- 当x和y都为0时说明进行了指令重排
                String result = "第" + i + "次 (" + x + "," + y + "）";
                System.err.println(result);
                break;
            }
        }
    }

    public static void shortWait(long interval) {
        long start = System.nanoTime();
        long end;
        do {
            end = System.nanoTime();
        } while (start + interval >= end);
    }

}