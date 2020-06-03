package com.yanwu.spring.cloud.common.demo.d03thread.t07lock;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 16:33.
 * <p>
 * description:
 * 交换器：两个线程相互交换数据，当第一个线程执行exchange函数后，该线程进入阻塞状态，等待第二个线程执行exchange函数进行数据交换
 */
@SuppressWarnings("all")
public class D061Exchanger {
    private static final Exchanger<User> EXCHANGER = new Exchanger<User>();

    public static void main(String[] args) {
        new Thread(() -> {
            User user = new User("yanwu", "xbf12138");
            System.out.println(Thread.currentThread().getName() + " begin user: " + user);
            try {
                user = EXCHANGER.exchange(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " exchange done user: " + user);
        }, "yanwu").start();
        new Thread(() -> {
            User user = new User("lotus", "lotus156412");
            System.out.println(Thread.currentThread().getName() + " begin user: " + user);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                user = EXCHANGER.exchange(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " exchange done user: " + user);
        }, "lotus").start();
    }

    @Data
    @AllArgsConstructor
    private static class User {
        private String username;
        private String password;
    }
}
