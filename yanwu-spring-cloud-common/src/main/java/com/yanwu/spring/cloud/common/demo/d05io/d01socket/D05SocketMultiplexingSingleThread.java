package com.yanwu.spring.cloud.common.demo.d05io.d01socket;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/15 13:28.
 * <p>
 * description: 单线程多路复用
 */
@Slf4j
public class D05SocketMultiplexingSingleThread {
    private ServerSocketChannel server = null;
    private Selector selector = null;
    private static final Integer PORT = 8080;

    public static void main(String[] args) throws Exception {
        D05SocketMultiplexingSingleThread socket = new D05SocketMultiplexingSingleThread();
        socket.start();
    }

    private void start() throws Exception {
        initServer();
        log.info("step1: new ServerSocket({})", PORT);
        while (true) {
            while (selector.select(500) > 0) {
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

    private void initServer() throws Exception {
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(PORT));
        server.configureBlocking(false);

        selector = Selector.open();

        server.register(selector, SelectionKey.OP_ACCEPT);
    }

}
