package com.yanwu.spring.cloud.common.demo.d09socket.day003;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-11-23 21:48:06.
 * <p>
 * describe: HTTP Demo
 */
@Slf4j
public class RawHttpServer {
    private ServerSocket serverSocket;
    private final Function<String, String> handler;

    public RawHttpServer(Function<String, String> handler) {
        this.handler = handler;
    }

    public void listen(int port) throws Exception {
        this.serverSocket = new ServerSocket(port);
        for (; ; ) {
            this.accept();
        }
    }

    private void accept() throws Exception {
        Socket accept = serverSocket.accept();
        new Thread(() -> {
            try {
                this.handler(accept);
            } catch (Exception e) {
                log.error("accept error.", e);
            }
        }).start();
    }

    private void handler(Socket socket) throws Exception {
        log.info("C socket created");
        try (InputStream inputStream = new DataInputStream(socket.getInputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            StringBuilder builder = new StringBuilder();
            String line = "";
            for (; ; ) {
                line = reader.readLine();
                if (StringUtils.isBlank(line)) {
                    break;
                }
                builder.append(line);
            }
            String request = builder.toString();
            log.info("server message: {}", request);
            writer.write(this.handler.apply(request));
            writer.flush();
        }
        socket.close();
    }

    public static void main(String[] args) throws Exception {
        RawHttpServer server = new RawHttpServer(request -> {
            return "HTTP/1.1 201 ok\n\nHello World\n";
        });
        server.listen(8080);
    }
}
