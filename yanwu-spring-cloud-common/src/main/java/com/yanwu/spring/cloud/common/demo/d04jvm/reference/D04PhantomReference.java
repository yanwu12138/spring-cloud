package com.yanwu.spring.cloud.common.demo.d04jvm.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-31 20:44:52.
 * <p>
 * describe:
 * 虚引用：当虚引用对象被回收时，会讲这个对象丢到ReferenceQueue队列中，并且得到系统通知
 */
@SuppressWarnings("all")
public class D04PhantomReference {

    private static final Integer SIZE = 1024 * 1024;
    private static final List<Object> LIST = new ArrayList<>();
    private static final ReferenceQueue<Reference> QUEUE = new ReferenceQueue<>();

    public static void main(String[] args) throws Exception {
        PhantomReference<Reference> reference = new PhantomReference<>(new Reference(), QUEUE);
        new Thread(() -> {
            for (; ; ) {
                LIST.add(new byte[SIZE]);
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                System.out.println(reference.get());
            }
        }).start();
        new Thread(() -> {
            for (; ; ) {
                java.lang.ref.Reference<? extends Reference> poll = QUEUE.poll();
                if (poll != null) {
                    System.out.println("phantomReference object: " + poll);
                }
            }
        }).start();
        TimeUnit.MILLISECONDS.sleep(500);
    }
}
