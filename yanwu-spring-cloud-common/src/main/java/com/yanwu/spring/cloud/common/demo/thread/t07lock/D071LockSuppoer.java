package com.yanwu.spring.cloud.common.demo.thread.t07lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 16:40.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class D071LockSuppoer {
    public static void main(String[] args) throws Exception {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                if (i == 5) {
                    // ===== 阻塞
                    LockSupport.park();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        TimeUnit.SECONDS.sleep(10);
        // ===== 停止阻塞
        LockSupport.unpark(thread);
    }
}
