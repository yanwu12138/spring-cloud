package com.yanwu.spring.cloud.common.demo.d09socket.day004;

import com.yanwu.spring.cloud.common.core.common.Encoding;
import lombok.Getter;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpParser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-11-23 22:53:53.
 * <p>
 * describe:
 */
@Getter
public class Request {

    private static final Pattern METHOD_REGEX = Pattern.compile("(GET|PUT|POST|DELETE|OPTIONS|TRACE|HEAD)");
    private final String requestBody;
    private final String method;
    private final Map<String, String> headers;

    public Request(Socket socket) throws Exception {
        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));

        String line = HttpParser.readLine(dataInputStream, Encoding.UTF_8);
        Matcher matcher = METHOD_REGEX.matcher(line);
        matcher.find();
        Header[] headers = HttpParser.parseHeaders(inputStream, Encoding.UTF_8);
        Map<String, String> map = new HashMap<>(headers.length);
        for (Header header : headers) {
            map.put(header.getName(), header.getValue());
        }
        StringBuilder body = new StringBuilder();
        char[] buffer = new char[1024];
        while (dataInputStream.available() > 0) {
            reader.read(buffer);
            body.append(buffer);
        }
        this.requestBody = body.toString();
        this.method = matcher.group();
        this.headers = map;

//        IOUtil.close(reader, dataInputStream, inputStream);
    }
}
