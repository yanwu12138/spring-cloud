package com.yanwu.spring.cloud.common.demo.d05io.d01socket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/15 11:46.
 * <p>
 * description:
 */
@Slf4j
public class D02SocketClient {

    private static final String IP = "127.0.0.1";
    private static final Integer PORT = 8080;

    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket(IP, PORT)) {
            socket.setSendBufferSize(20);
            socket.setTcpNoDelay(true);
            try (OutputStream os = socket.getOutputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    String line = reader.readLine();
                    if (StringUtils.isNotBlank(line)) {
                        log.info("socket client write: {}", line);
                        os.write(line.getBytes());
                    }
                }
            }
        }
    }
}
