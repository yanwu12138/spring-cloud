package com.yanwu.spring.cloud.common.demo.d09socket.day001;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
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

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);
        for (; ; ) {
            Socket accept = serverSocket.accept();
            log.info("A socket created");
            try (InputStream inputStream = new DataInputStream(accept.getInputStream());
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(accept.getOutputStream()))) {
                StringBuilder builder = new StringBuilder();
                String line = "";
                while (StringUtils.isNotBlank(line = reader.readLine())) {
                    builder.append(line);
                }
                log.info("server message: {}", builder.toString());
                writer.write("HTTP/1.1 200 ok\n\n Hello World\n");
                writer.flush();
            }
            accept.close();
        }
    }

}
