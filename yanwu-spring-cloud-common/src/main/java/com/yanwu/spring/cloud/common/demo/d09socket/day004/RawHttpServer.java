package com.yanwu.spring.cloud.common.demo.d09socket.day004;

import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-11-23 21:48:06.
 * <p>
 * describe: HTTP Demo
 */
@Slf4j
public class RawHttpServer {
    private ServerSocket serverSocket;
    private final IHandlerInterface handler;

    public RawHttpServer(IHandlerInterface handler) {
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
        this.handler.handler(new Request(socket), new Response(socket));
    }

    public static void main(String[] args) throws Exception {
        RawHttpServer server = new RawHttpServer((request, response) -> response.send("Hello world\n"));
        server.listen(8080);
    }

}
