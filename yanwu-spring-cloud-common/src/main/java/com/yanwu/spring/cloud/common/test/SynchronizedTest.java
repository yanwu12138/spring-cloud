package com.yanwu.spring.cloud.common.test;

import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author XuBaofeng.
 * @date 2024/2/28 16:01.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class SynchronizedTest {

    private static final Object LOCK = new Object();
    private static final Object LOCK1 = new Object();
    private static final Object LOCK2 = new Object();

    public synchronized void test1(String content, long sleep) {
        log.info("run test1 - 1, content: {}", content);
        ThreadUtil.sleep(sleep);
        log.info("run test1 - 2, content: {}", content);
    }

    public void test2(String content, long sleep) {
        synchronized (LOCK) {
            log.info("run test2 - 1, content: {}", content);
            ThreadUtil.sleep(sleep);
            log.info("run test2 - 2, content: {}", content);
        }
    }

    public void test3(boolean lockFlag, String content, long sleep) {
        synchronized (lockFlag ? LOCK1 : LOCK2) {
            log.info("run test3 - 1, content: {}", content);
            ThreadUtil.sleep(sleep);
            log.info("run test3 - 2, content: {}", content);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println();
        log.info("---------- test01 ----------");
        test01();
        ThreadUtil.sleep(10_000L);
        System.out.println();
        log.info("---------- test02 ----------");
        test02();
        ThreadUtil.sleep(10_000L);
        System.out.println();
        log.info("---------- test03 ----------");
        test03();
        ThreadUtil.sleep(10_000L);
        System.out.println();
        log.info("---------- test04 ----------");
        test04();
        ThreadUtil.sleep(10_000L);
        System.out.println();
        log.info("---------- test05 ----------");
        test05();
        ThreadUtil.sleep(10_000L);
    }

    public static void test01() {
        new Thread(() -> new SynchronizedTest().test1("AAAA", 3000L)).start();
        new Thread(() -> new SynchronizedTest().test1("BBBB", 5000L)).start();
    }

    public static void test02() {
        new Thread(() -> new SynchronizedTest().test2("CCCC", 3000L)).start();
        new Thread(() -> new SynchronizedTest().test2("DDDD", 5000L)).start();
    }

    public static void test03() {
        SynchronizedTest instance = new SynchronizedTest();
        new Thread(() -> instance.test1("EEEE", 3000L)).start();
        new Thread(() -> instance.test1("FFFF", 5000L)).start();
    }

    public static void test04() {
        SynchronizedTest instance = new SynchronizedTest();
        new Thread(() -> instance.test1("GGGG", 3000L)).start();
        new Thread(() -> instance.test1("HHHH", 5000L)).start();
    }

    public static void test05() {
        SynchronizedTest instance = new SynchronizedTest();
        new Thread(() -> instance.test3(true, "IIII", 3000L)).start();
        new Thread(() -> instance.test3(false, "JJJJ", 5000L)).start();
    }
}
