package com.yanwu.spring.cloud.common.demo.d05io.d01socket;

import com.yanwu.spring.cloud.common.utils.ByteUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/29 15:40.
 * <p>
 * description: 零拷贝原理理解
 */
public class D00ZeroCopy {
    private static final Integer PORT = 8888;
    private static final String HOST = "127.0.0.1";
    private static final Integer BYTE_SIZE = 1024 * 10;
    private static final String FILE_PATH = "F:\\document\\协议文档.zip";

    /**
     * 传统IO，就是死循环不断地去监听8888端口有没有数据传递上来：
     * 当有数据时：读取数据，继续循环
     */
    private static class IoServer {
        public static void main(String[] args) throws Exception {
            ServerSocket server = new ServerSocket(PORT);
            for (;;) {
                Socket socket = server.accept();
                try (InputStream inputStream = new DataInputStream(socket.getInputStream())) {
                    byte[] bytes = new byte[BYTE_SIZE];
                    int read;
                    while ((read = inputStream.read(bytes, 0, BYTE_SIZE)) != -1) {
                        System.out.println(ByteUtil.bytesToHexStrPrint(bytes));
                    }
                }
            }
        }
    }

    /**
     * 发送总字节数： 256359544, 耗时： 24252
     */
    private static class IoClient {
        public static void main(String[] args) throws Exception {
            try (Socket socket = new Socket(HOST, PORT);
                 InputStream inputStream = new FileInputStream(FILE_PATH);
                 OutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
                byte[] bytes = new byte[BYTE_SIZE];
                long start = System.currentTimeMillis();
                long read = 0, total = 0;
                while ((read = inputStream.read(bytes)) >= 0) {
                    total += read;
                    outputStream.write(bytes);
                }
                long end = System.currentTimeMillis();
                System.out.println("发送总字节数： " + total + ", 耗时： " + (end - start));
            }
        }
    }

    // ==================== NIO ==================== //

    /**
     *
     */
    private static class NioServer {

    }

    private static class NioClient {
        public static void main(String[] args) throws Exception {
            try (SocketChannel socket = SocketChannel.open()) {
                socket.connect(new InetSocketAddress(HOST, PORT));
                socket.configureBlocking(true);
                try (FileChannel channel = new FileInputStream(FILE_PATH).getChannel()) {
                    long start = System.currentTimeMillis();
                    long total = channel.transferTo(0, channel.size(), socket);
                    long end = System.currentTimeMillis();
                    System.out.println("发送总字节数： " + total + ", 耗时： " + (end - start));
                }
            }
        }
    }
}
