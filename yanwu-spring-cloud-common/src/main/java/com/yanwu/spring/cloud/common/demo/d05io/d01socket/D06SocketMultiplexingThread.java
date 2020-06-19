package com.yanwu.spring.cloud.common.demo.d05io.d01socket;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/15 13:45.
 * <p>
 * description:
 */
@Slf4j
public class D06SocketMultiplexingThread {
    private static final Integer PORT = 8080;
    private ServerSocketChannel server = null;
    private Selector selector1 = null;
    private Selector selector2 = null;
    private Selector selector3 = null;

    public static void main(String[] args) throws Exception {
        D06SocketMultiplexingThread socket = new D06SocketMultiplexingThread();
        socket.initServer();
        NIOThread nioThread1 = new NIOThread(socket.selector1, 2);
        NIOThread nioThread2 = new NIOThread(socket.selector2);
        NIOThread nioThread3 = new NIOThread(socket.selector3);
        new Thread(nioThread1).start();
        new Thread(nioThread2).start();
        new Thread(nioThread3).start();
        log.info("step1: new ServerSocket({})", PORT);
    }

    private void initServer() throws Exception {
        server = ServerSocketChannel.open();
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(PORT));
        selector1 = Selector.open();
        selector2 = Selector.open();
        selector3 = Selector.open();
        server.register(selector1, SelectionKey.OP_ACCEPT);
    }

    @Data
    @Accessors(chain = true)
    private static class NIOThread implements Runnable {
        private Selector selector;
        private Integer selectors;
        private int id = 0;
        private volatile static BlockingQueue<SocketChannel>[] queue;
        private static AtomicInteger idx = new AtomicInteger();

        NIOThread(Selector selector, int selectors) {
            this.selector = selector;
            this.selectors = selectors;
            queue = new LinkedBlockingDeque[selectors];
            for (int i = 0; i < selectors; i++) {
                queue[i] = new LinkedBlockingDeque<>();
            }
            log.info("boss start...");
        }

        NIOThread(Selector selector) {
            this.selector = selector;
            id = idx.getAndIncrement() % selectors;
            log.info("worker: {} start...", id);
        }

        @Override
        @SneakyThrows
        public void run() {
            while (true) {
                while (selector.select(10) > 0) {
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = keys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey next = iterator.next();
                        iterator.remove();
                        if (next.isAcceptable()) {
                            acceptHandler(next);
                        } else if (next.isReadable()) {
                            readHandler(next);
                        }
                    }
                }
            }
        }

        private void readHandler(SelectionKey key) throws Exception {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            buffer.clear();
            while (channel.read(buffer) > 0) {
                buffer.flip();
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                log.info("client message: {}", new String(bytes));
                channel.write(buffer);
            }
        }

        private void acceptHandler(SelectionKey key) throws Exception {
            ByteBuffer buffer = ByteBuffer.allocateDirect(8192);
            SocketChannel client = ((ServerSocketChannel) key.channel()).accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ, buffer);
            log.info("step2: client: IP: {}, PORT: {}", client.socket().getInetAddress(), client.socket().getPort());
        }
    }

}
