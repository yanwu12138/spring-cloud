package com.yanwu.spring.cloud.common.demo.d03thread.t07lock;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/28 15:48.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public class D031Phaser {
    private static final Random RANDOM = new Random();
    private static final Phaser PHASER = new MarriagePhaser();
    private static final CountDownLatch LATCH = new CountDownLatch(7);

    public static void main(String[] args) {
        PHASER.bulkRegister(7);
        for (int i = 0; i < 5; i++) {
            new Thread(new Person("person_" + i)).start();
        }
        new Thread(new Person("新郎")).start();
        new Thread(new Person("新娘")).start();
        try {
            LATCH.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PHASER.bulkRegister(7);
        new Thread(new Person("大力娃")).start();
        new Thread(new Person("隐身娃")).start();
        new Thread(new Person("千里眼娃")).start();
        new Thread(new Person("顺风耳娃")).start();
        new Thread(new Person("喷火娃")).start();
        new Thread(new Person("喷水娃")).start();
        new Thread(new Person("金刚娃")).start();
    }

    private static void milliSleep(int milli) {
        try {
            TimeUnit.MILLISECONDS.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class MarriagePhaser extends Phaser {
        @Override
        protected boolean onAdvance(int phaser, int registerParties) {
            switch (phaser) {
                case 0:
                    System.out.println("所有人到齐了！" + registerParties);
                    System.out.println("----------");
                    return false;
                case 1:
                    System.out.println("所有人吃完了！" + registerParties);
                    System.out.println("----------");
                    return false;
                case 2:
                    System.out.println("所有人离开了！" + registerParties);
                    System.out.println("----------");
                    return false;
                case 3:
                    System.out.println("婚礼结束！新郎新娘抱抱！" + registerParties);
                    System.out.println("----------");
                    return false;
                case 4:
                    System.out.println("娃娃们生出来了！一家欢乐！" + registerParties);
                    return true;
                default:
                    return true;
            }
        }
    }

    private static class Person implements Runnable {
        String name;

        public Person(String name) {
            this.name = name;
        }

        public void arrive() {
            if (name.endsWith("娃")) {
                return;
            }
            milliSleep(RANDOM.nextInt(1000));
            System.out.printf("%s 到达现场！\n", name);
            PHASER.arriveAndAwaitAdvance();
        }

        public void eat() {
            if (name.endsWith("娃")) {
                return;
            }
            milliSleep(RANDOM.nextInt(1000));
            System.out.printf("%s 吃完!\n", name);
            PHASER.arriveAndAwaitAdvance();
        }

        public void leave() {
            if (name.endsWith("娃")) {
                return;
            }
            milliSleep(RANDOM.nextInt(1000));
            System.out.printf("%s 离开！\n", name);
            PHASER.arriveAndAwaitAdvance();
        }

        private void hug() {
            if (name.endsWith("娃")) {
                return;
            }
            if (name.equals("新郎") || name.equals("新娘")) {
                milliSleep(RANDOM.nextInt(1000));
                System.out.printf("%s 洞房！\n", name);
                PHASER.arriveAndAwaitAdvance();
            } else {
                PHASER.arriveAndDeregister();
            }
        }

        private void raw() {
            if (!name.endsWith("娃")) {
                return;
            }
            milliSleep(RANDOM.nextInt(1000));
            System.out.printf("%s 出生！\n", name);
            PHASER.arrive();
            PHASER.arriveAndAwaitAdvance();
        }

        @Override
        public void run() {
            arrive();
            eat();
            leave();
            hug();
            raw();
            LATCH.countDown();
        }
    }
}
