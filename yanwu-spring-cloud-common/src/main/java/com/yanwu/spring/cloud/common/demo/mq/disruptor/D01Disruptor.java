package com.yanwu.spring.cloud.common.demo.mq.disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-05-30 22:16:32.
 * <p>
 * describe: disruptor: https://note.youdao.com/web/#/file/WEBb0d6990538084561ba52df1c1c9de02e/markdown/WEBeabeba52fadaa5a2418a4806b7475058/
 */
@Slf4j
@SuppressWarnings("all")
public class D01Disruptor {
    // ----- 环的长度
    private static final Integer BUFFER_SIZE = 1024;

    /**
     * 基础写法
     */
    public static void testDisruptor01() {
        // ----- 创建工厂
        StringEventFactory factory = new StringEventFactory();
        Disruptor<StringEvent> disruptor = new Disruptor<>(factory, BUFFER_SIZE, Executors.defaultThreadFactory());
        // ----- 给disruptor绑定handler
        StringEventHandler handler = new StringEventHandler();
        disruptor.handleEventsWith(handler);
        // ----- 启动
        disruptor.start();
        // ----- 拿到环形队列
        RingBuffer<StringEvent> ringBuffer = disruptor.getRingBuffer();
        for (int i = 0; i < BUFFER_SIZE + 2; i++) {
            // ----- 找到下一个可用的位置
            long sequence = ringBuffer.next();
            try {
                // ----- 根据位置拿到event
                StringEvent event = ringBuffer.get(sequence);
                event.set(get());
            } finally {
                // ----- 发布
                ringBuffer.publish(sequence);
            }
        }
    }

    /**
     * lambda0.5
     */
    public static void testDisruptor02() {
        Disruptor<StringEvent> disruptor = new Disruptor<>(new StringEventFactory(), BUFFER_SIZE, DaemonThreadFactory.INSTANCE);
        disruptor.handleEventsWith(new StringEventHandler());
        disruptor.start();
        RingBuffer<StringEvent> ringBuffer = disruptor.getRingBuffer();
        ringBuffer.publishEvent(new EventTranslator<StringEvent>() {
            @Override
            public void translateTo(StringEvent event, long sequence) {
                event.set(get());
            }
        });
        ringBuffer.publishEvent(new EventTranslatorOneArg<StringEvent, String>() {
            @Override
            public void translateTo(StringEvent event, long sequence, String value) {
                event.set(value);
            }
        }, get());
        ringBuffer.publishEvent(new EventTranslatorTwoArg<StringEvent, String, String>() {
            @Override
            public void translateTo(StringEvent event, long sequence, String value1, String value2) {
                event.set(cover(value1, value2));
            }
        }, get(), get());
        ringBuffer.publishEvent(new EventTranslatorVararg<StringEvent>() {
            @Override
            public void translateTo(StringEvent event, long sequence, Object... values) {
                event.set(cover(values));
            }
        }, get(), get(), get());
    }

    /**
     * lambda
     */
    public static void testDisruptor03() {
        // ----- 创建disruptor
        Disruptor<StringEvent> disruptor = new Disruptor<>(StringEvent::new, BUFFER_SIZE, DaemonThreadFactory.INSTANCE);
        // ----- 绑定handler
        disruptor.handleEventsWith((event, sequence, endOfBatch) ->
                log.info("thread: {}, event: {}, sequence: {}, endOfBatch: {}",
                        Thread.currentThread().getName(), event, sequence, endOfBatch));
        // ----- 启动
        disruptor.start();
        // ----- 获取环形队列
        RingBuffer<StringEvent> ringBuffer = disruptor.getRingBuffer();
        // ----- 通过各种形式丢数据进去
        ringBuffer.publishEvent((event, sequnce) -> event.set(get()));
        ringBuffer.publishEvent((event, squence, value) -> event.set(value), get());
        ringBuffer.publishEvent((event, sequence, value1, value2) -> event.set(cover(value1, value2)), get(), get());
        ringBuffer.publishEvent((event, sequnce, values) -> event.set(cover(values)), get(), get(), get(), get());
    }

    /**
     * 单例模式：只有一个生产者的时候能够使用，多个生产者使用单例模式会产生消息被丢掉的现象
     */
    public static void testDisruptor04() {
        Disruptor<StringEvent> disruptor = new Disruptor<>(StringEvent::new, BUFFER_SIZE,
                Executors.defaultThreadFactory(), ProducerType.SINGLE, new BlockingWaitStrategy());
        disruptor.handleEventsWith(
                (event, sequence, endOfBatch) ->
                        log.info("thread: {}, event: {}, sequence: {}, endOfBatch: {}",
                                Thread.currentThread().getName(), event, sequence, endOfBatch),
                (event, sequence, endOfBatch) ->
                        log.info("thread: {}, event: {}, sequence: {}, endOfBatch: {}",
                                Thread.currentThread().getName(), event, sequence, endOfBatch),
                (event, sequence, endOfBatch) ->
                        log.info("thread: {}, event: {}, sequence: {}, endOfBatch: {}",
                                Thread.currentThread().getName(), event, sequence, endOfBatch));
        disruptor.start();
        RingBuffer<StringEvent> ringBuffer = disruptor.getRingBuffer();
        final int threadCoune = 50;
        CyclicBarrier barrier = new CyclicBarrier(threadCoune);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < threadCoune; i++) {
            final long threadNum = i;
            threadPool.submit(() -> {
                try {
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < 100; j++) {
                    ringBuffer.publishEvent((event, sequence) -> event.set(get()));
                }
            });
        }
        threadPool.shutdown();
    }

    /**
     * 单例模式：只有一个生产者的时候能够使用，多个生产者使用单例模式会产生消息被丢掉的现象
     */
    public static void testDisruptor() {
        Disruptor<StringEvent> disruptor = new Disruptor<>(StringEvent::new, BUFFER_SIZE,
                Executors.defaultThreadFactory(), ProducerType.MULTI, new BlockingWaitStrategy());
        disruptor.handleEventsWith(
                (event, sequence, endOfBatch) ->
                        log.info("thread: {}, event: {}, sequence: {}, endOfBatch: {}",
                                Thread.currentThread().getName(), event, sequence, endOfBatch),
                (event, sequence, endOfBatch) ->
                        log.info("thread: {}, event: {}, sequence: {}, endOfBatch: {}",
                                Thread.currentThread().getName(), event, sequence, endOfBatch),
                (event, sequence, endOfBatch) ->
                        log.info("thread: {}, event: {}, sequence: {}, endOfBatch: {}",
                                Thread.currentThread().getName(), event, sequence, endOfBatch));
        disruptor.start();
        RingBuffer<StringEvent> ringBuffer = disruptor.getRingBuffer();
        final int threadCoune = 2;
        CyclicBarrier barrier = new CyclicBarrier(threadCoune);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < threadCoune; i++) {
            final long threadNum = i;
            threadPool.submit(() -> {
                try {
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < 100; j++) {
                    ringBuffer.publishEvent((event, sequence) -> event.set(Thread.currentThread().getName()));
                }
            });
        }
        threadPool.shutdown();
    }

    private static final String get() {
        return Thread.currentThread().getName();
    }

    private static final String cover(Object... values) {
        StringBuffer buffer = new StringBuffer();
        for (Object obj : values) {
            buffer.append(String.valueOf(obj));
        }
        return buffer.toString();
    }
}
