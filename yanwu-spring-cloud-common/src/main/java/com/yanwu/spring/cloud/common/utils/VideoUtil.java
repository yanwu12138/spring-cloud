package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * @author Baofeng Xu
 * @date 2023-03-27 027 17:34:38.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class VideoUtil {
    private static final int DEFAULT_LENGTH = 1024 * 1024;

    private VideoUtil() {
        throw new UnsupportedOperationException("VideoUtil should never be instantiated");
    }

    public static void play(HttpServletRequest request, HttpServletResponse response, String filepath) throws IOException {
        response.reset();
        // ===== 检查文件
        if (StringUtils.isBlank(filepath)) {
            log.error("play video failed, because filepath is empty.");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file = new File(filepath);
        if (!FileUtil.fileExists(file) || !file.isFile()) {
            log.error("play video failed, because file is not exists. file: {}", filepath);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // ===== 读取文件（只读）
        try (RandomAccessFile randomFile = new RandomAccessFile(file, "r")) {
            long contentLength = randomFile.length();
            String rangeStr = request.getHeader(HttpHeaders.RANGE);
            int start = 0, end = 0;
            if (StringUtils.isNotBlank(rangeStr) && rangeStr.startsWith("bytes=")) {
                String[] values = rangeStr.split("=")[1].split("-");
                start = Integer.parseInt(values[0]);
                if (values.length > 1) {
                    end = Integer.parseInt(values[1]);
                }
            }
            int requestSize;
            if (end != 0 && end > start) {
                requestSize = end - start + 1;
            } else {
                requestSize = Integer.MAX_VALUE;
            }
            byte[] buffer = new byte[DEFAULT_LENGTH];
            // ----- 第一次请求只返回content length来让客户端请求多次实际数据
            if (StringUtils.isBlank(rangeStr)) {
                responseHeader(response, file.getName(), HttpServletResponse.SC_OK);
                response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
            } else {
                // ----- 以后的多次以断点续传的方式来返回视频数据
                responseHeader(response, file.getName(), HttpServletResponse.SC_PARTIAL_CONTENT);
                long requestStart = 0, requestEnd = 0;
                String[] ranges = rangeStr.split("=");
                if (ranges.length > 1) {
                    String[] rangeDatas = ranges[1].split("-");
                    requestStart = Integer.parseInt(rangeDatas[0]);
                    if (rangeDatas.length > 1) {
                        requestEnd = Integer.parseInt(rangeDatas[1]);
                    }
                }
                long length;
                if (requestEnd > 0) {
                    length = requestEnd - requestStart + 1;
                    response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(length));
                    response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + requestStart + "-" + requestEnd + "/" + contentLength);
                } else {
                    length = contentLength - requestStart;
                    response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(length));
                    response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + requestStart + "-" + (contentLength - 1) + "/" + contentLength);
                }
            }
            try (ServletOutputStream stream = response.getOutputStream()) {
                int needSize = requestSize;
                randomFile.seek(start);
                while (needSize > 0) {
                    int len = randomFile.read(buffer);
                    if (needSize < buffer.length) {
                        stream.write(buffer, 0, needSize);
                    } else {
                        stream.write(buffer, 0, len);
                        if (len < buffer.length) {
                            break;
                        }
                    }
                    needSize -= buffer.length;
                }
            } catch (Exception ignored) {
            }
        }
    }

    private static void responseHeader(HttpServletResponse response, String filename, int status) {
        response.setStatus(status);
        response.setHeader(HttpHeaders.ETAG, filename);
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "video/mp4");
        response.setHeader(HttpHeaders.LAST_MODIFIED, new Date().toString());
    }

}