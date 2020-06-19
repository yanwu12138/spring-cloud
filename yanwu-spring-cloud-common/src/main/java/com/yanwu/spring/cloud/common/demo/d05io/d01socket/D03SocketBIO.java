package com.yanwu.spring.cloud.common.demo.d05io.d01socket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/15 11:37.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("all")
public class D03SocketBIO {

    private static final String IP = "localhost";
    private static final Integer PORT = 8080;

    public static void main(String[] args) throws Exception {
        try (ServerSocket server = new ServerSocket(PORT, 20)) {
            log.info("step1: new ServerSocket({})", PORT);
            while (true) {
                // ----- 这里等待客户端的连接，在没有客户端连接进来时，始终处于阻塞状态
                Socket client = server.accept();
                new Thread(() -> {
                    log.info("step2: client: IP: {}, PORT: {}", client.getInetAddress(), client.getPort());
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
                        while (true) {
                            // ----- 这里等待客户端发送数据，在客户端没有发送数据的时候，始终处于阻塞状态
                            String line = reader.readLine();
                            if (StringUtils.isNotBlank(line)) {
                                log.info("client message: {}", line);
                            }
                        }
                    } catch (Exception e) {
                        log.error("socket io error.", e);
                    }
                }).start();
            }
        }
    }
}
