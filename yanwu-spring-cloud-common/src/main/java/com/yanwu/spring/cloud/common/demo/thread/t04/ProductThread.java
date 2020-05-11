package com.yanwu.spring.cloud.common.demo.thread.t04;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-04-23 23:22:30.
 * <p>
 * describe: 生产者消费者模型
 */
@Slf4j
public class ProductThread {
    public static final Integer SIZE = 5;

    public static void main(String[] args) throws Exception {
        Storage storage = new Storage();
        new Thread(new Consumer(storage), "消费者1号").start();
        new Thread(new Consumer(storage), "消费者2号").start();
        TimeUnit.MILLISECONDS.sleep(100);
        new Thread(new Producer(storage), "生产者1号").start();
        new Thread(new Producer(storage), "生产者2号").start();
        log.info("main done.");
    }
}

/**
 * 生产者
 */
class Producer implements Runnable {
    private final Storage storage;

    public Producer(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        Random random = new Random();
        int size = ProductThread.SIZE;
        while (size > 0) {
            size--;
            storage.push(new Product(size, "product: " + random.nextInt(10)));
        }
    }
}

/**
 * 消费者
 */
@Slf4j
class Consumer implements Runnable {
    private final Storage storage;

    Consumer(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        int size = ProductThread.SIZE;
        while (size > 0) {
            size--;
            storage.pop();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (Exception e) {
                log.error("consumer exception: ", e);
            }
        }
    }
}

/**
 * 商品
 */
@Data
@AllArgsConstructor
class Product {
    private Integer id;
    private String name;
}

/**
 * 仓库
 */
@Slf4j
class Storage {
    private final Product[] products = new Product[ProductThread.SIZE];
    private Integer top = 0;

    public synchronized void push(Product product) {
        while (top == products.length) {
            try {
                log.info("producer: {} wait.", Thread.currentThread().getName());
                wait();
            } catch (Exception e) {
                log.error("push exception: ", e);
            }
        }
        products[top++] = product;
        log.info("producer: {} push product: {}, notifyAll", Thread.currentThread().getName(), product);
        notifyAll();
    }

    public synchronized void pop() {
        while (top == 0) {
            try {
                log.info("consumer: {} wait.", Thread.currentThread().getName());
                wait();
            } catch (Exception e) {
                log.error("pop exception: ", e);
            }
        }
        Product product = products[top - 1];
        products[--top] = null;
        log.info("consumer: {} pop  product: {}, notifyAll", Thread.currentThread().getName(), product);
        notifyAll();
    }
}