package com.yanwu.spring.cloud.common.demo.d09socket.day004;


import org.apache.commons.collections4.MapUtils;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-11-23 22:54:00.
 * <p>
 * describe:
 */
public class Response {
    private final Socket socket;
    private final Integer status;
    private static Map<Integer, String> codeMap;

    public Response(Socket socket) {
        this.socket = socket;
        this.status = 200;
        if (MapUtils.isEmpty(codeMap)) {
            codeMap = new HashMap<>();
            codeMap.put(200, "OK");
        }
    }

    public void send(String message) throws Exception {
        String res = "HTTP/1.1 " + this.status + codeMap.get(this.status) + "\n\n";
        res += message;
        this.sendRaw(res);
    }

    public void sendRaw(String message) throws Exception {
        OutputStream outputStream = socket.getOutputStream();
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        BufferedWriter writer = new BufferedWriter(streamWriter);

        writer.write(message);
        writer.flush();
        IOUtil.close(writer, streamWriter, outputStream, socket);
    }
}
