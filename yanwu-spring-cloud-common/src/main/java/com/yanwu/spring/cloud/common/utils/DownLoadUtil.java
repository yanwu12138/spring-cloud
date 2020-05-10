package com.yanwu.spring.cloud.common.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.util.Assert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/8 17:26.
 * <p>
 * description: 文件分片下载
 */
@Slf4j
public class DownLoadUtil {

    /*** 每个线程下载的字节数 */
    private static final Long UNIT_SIZE = 1000 * 1024L;
    private static final CloseableHttpClient HTTP_CLIENT;
    private static final Executor EXECUTOR;

    private DownLoadUtil() {
    }

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        HTTP_CLIENT = HttpClients.custom().setConnectionManager(connectionManager).build();
        EXECUTOR = Executors.newFixedThreadPool(8);
    }

    /**
     * 下载
     *
     * @param fileUrl   资源路径
     * @param localPath 文件存放路径
     */
    public static void doDownload(String fileUrl, String localPath) throws Exception {
        File file = new File(localPath);
        if (file.exists() && file.isFile()) {
            Assert.isTrue(file.delete(), "file delete error.");
        }
        Assert.isTrue(file.getParentFile().mkdirs(), "file parent create error.");
        Assert.isTrue(file.createNewFile(), "file create error.");

        // ----- 获取远程文件的大小，根据文件大小决定线程的个数
        log.info("download file begin, localPath: {}, fileUrl: {}", localPath, fileUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(fileUrl).openConnection();
        httpConnection.setRequestMethod("HEAD");
        int responseCode = httpConnection.getResponseCode();
        Assert.isTrue((responseCode <= 400), "get remote file size error. code: " + responseCode);
        long fileSize = Long.parseLong(httpConnection.getHeaderField("Content-Length"));
        Assert.isTrue((fileSize > 0), "get remote file size error. size is zero");
        long threadCount = Math.floorDiv(fileSize, UNIT_SIZE);
        threadCount = fileSize == threadCount * UNIT_SIZE ? threadCount : threadCount + 1;

        // ----- 根据threadCount开始下载文件
        CountDownLatch end = new CountDownLatch((int) threadCount);
        long start = System.currentTimeMillis();
        long offset = 0, size = 0;
        for (int i = 0; i < threadCount; i++) {
            EXECUTOR.execute(DownLoadTask.getInstance(fileUrl, localPath, offset, size, end, HTTP_CLIENT));
            offset += size;
        }
        // 如果远程文件尺寸小于等于unitSize
        if (fileSize <= UNIT_SIZE) {
            EXECUTOR.execute(DownLoadTask.getInstance(fileUrl, localPath, offset, fileSize, end, HTTP_CLIENT));
        } else {
            // 如果远程文件尺寸大于unitSize
            for (int i = 1; i < threadCount; i++) {
                EXECUTOR.execute(DownLoadTask.getInstance(fileUrl, localPath, offset, UNIT_SIZE, end, HTTP_CLIENT));
                offset += UNIT_SIZE;
            }
            // 如果不能整除，则需要再创建一个线程下载剩余字节
            if (fileSize % UNIT_SIZE != 0) {
                EXECUTOR.execute(DownLoadTask.getInstance(fileUrl, localPath, offset, fileSize - UNIT_SIZE * (threadCount - 1), end, HTTP_CLIENT));
            }
        }
        try {
            end.await();
        } catch (InterruptedException e) {
            log.error("downLoadUtil await error.", e);
        }
        log.info("download file done！localPath: {}, time: {}S", localPath, (System.currentTimeMillis() - start) / 1000);
    }

    /**
     * 文件下载任务
     */
    @Slf4j
    private static class DownLoadTask implements Runnable {
        /*** 待下载的文件 */
        private String url = null;
        /*** 本地文件名 */
        private String fileName = null;
        /*** 偏移量 */
        private long offset = 0;
        /*** 分配给本线程的下载字节数 */
        private long length = 0;

        private CountDownLatch end;
        private CloseableHttpClient httpClient;
        private HttpContext context;

        @Override
        @SneakyThrows
        public void run() {
            HttpGet httpGet = new HttpGet(this.url);
            httpGet.addHeader("Range", "bytes=" + this.offset + "-" + (this.offset + this.length - 1));
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            File newFile = new File(fileName);
            try (RandomAccessFile raf = new RandomAccessFile(newFile, "rw");
                 BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent())) {
                int read;
                byte[] bytes = new byte[1024];
                while ((read = bis.read(bytes, 0, bytes.length)) != -1) {
                    raf.seek(this.offset);
                    raf.write(bytes, 0, read);
                    this.offset = this.offset + read;
                }
            } finally {
                end.countDown();
                log.info(end.getCount() + " is go on!");
            }
        }

        static DownLoadTask getInstance(String url, String fileName, long offset, long length, CountDownLatch end, CloseableHttpClient httpClient) {
            DownLoadTask result = new DownLoadTask();
            result.url = url;
            result.offset = offset;
            result.length = length;
            result.fileName = fileName;
            result.end = end;
            result.httpClient = httpClient;
            result.context = new BasicHttpContext();
            return result;
        }

        private DownLoadTask() {
        }
    }
}