package com.yanwu.spring.cloud.common.demo.d05io.d01socket;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/15 13:11.
 * <p>
 * description:
 */
@Slf4j
public class D04SocketNIO {
    private static final Integer PORT = 8080;

    public static void main(String[] args) throws Exception {
        LinkedList<SocketChannel> clients = new LinkedList<>();
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.bind(new InetSocketAddress(PORT));
            server.configureBlocking(false);
            log.info("step1: new ServerSocket({})", PORT);
            while (true) {
                // ----- 检查是否有新的连接
                SocketChannel client = server.accept();
                if (client != null) {
                    log.info("step2: client: IP: {}, PORT: {}", client.socket().getInetAddress(), client.socket().getPort());
                    client.configureBlocking(false);
                    clients.add(client);
                }
                // ----- 检查是否有数据
                ByteBuffer buffer = ByteBuffer.allocateDirect(4096);
                for (SocketChannel channel : clients) {
                    int read = channel.read(buffer);
                    if (read > 0) {
                        buffer.flip();
                        byte[] bytes = new byte[buffer.limit()];
                        buffer.get(bytes);
                        log.info("client message: {}", new String(bytes));
                        buffer.clear();
                    }
                }
            }
        }
    }

}
