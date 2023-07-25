package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.Contents;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/8 17:26.
 * <p>
 * description: 文件分片下载
 */
@Slf4j
@SuppressWarnings("unused")
public class DownLoadUtil {

    /*** 文件下载线程池 ***/
    private static final ThreadPoolTaskExecutor EXECUTOR;
    /*** 每个线程下载的字节数: 10M */
    private static final Long UNIT_SIZE = 10 * 1024 * 1024L;

    static {
        EXECUTOR = new ThreadPoolTaskExecutor();
        // ----- 设置核心线程数
        EXECUTOR.setCorePoolSize(10);
        // ----- 设置最大线程数
        EXECUTOR.setMaxPoolSize(20);
        // ----- 设置队列容量
        EXECUTOR.setQueueCapacity(Integer.MAX_VALUE);
        // ----- 设置线程活跃时间（秒）
        EXECUTOR.setKeepAliveSeconds(120);
        // ----- 设置默认线程名称
        EXECUTOR.setThreadNamePrefix("down-pool-");
        // ----- 设置拒绝策略
        EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // ----- 执行初始化
        EXECUTOR.initialize();
    }

    private DownLoadUtil() {
        throw new UnsupportedOperationException("DownLoadUtil should never be instantiated");
    }

    /**
     * 下载
     *
     * @param fileUrl   资源路径
     * @param localPath 文件路径
     */
    public static long download(String fileUrl, String localPath) throws Exception {
        // ----- 获取远程文件的大小，根据文件大小决定线程的个数
        log.info("download file begin, localPath: {}, fileUrl: {}", localPath, fileUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(fileUrl).openConnection();
        httpConnection.setRequestMethod("HEAD");
        int responseCode = httpConnection.getResponseCode();
        Assert.isTrue((responseCode <= 400), "get remote file size error. code: " + responseCode);
        long fileSize = Long.parseLong(httpConnection.getHeaderField(HttpHeaders.CONTENT_LENGTH));
        final long fileSizeResult = fileSize;
        Assert.isTrue((fileSize > 0), "get remote file size error, size is zero.");
        long threadCount = Math.floorDiv(fileSize, UNIT_SIZE);
        threadCount = fileSize == threadCount * UNIT_SIZE ? threadCount : threadCount + 1;

        // ----- 检查文件[父目录是否存在 && 文件是否存在]
        File file = new File(localPath);
        if (FileUtil.fileExists(file)) {
            Assert.isTrue(file.delete(), "file delete error.");
        }
        FileUtil.checkDirectoryPath(file.getParentFile());
        Assert.isTrue(file.createNewFile(), "file create error.");

        // ----- 根据threadCount开始下载文件
        CountDownLatch end = new CountDownLatch((int) threadCount);
        long offset = 0, start = System.currentTimeMillis();
        while (fileSize > 0) {
            long length = fileSize > UNIT_SIZE ? UNIT_SIZE : fileSize;
            EXECUTOR.execute(DownLoadTask.getInstance(fileUrl, localPath, offset, length, end));
            fileSize -= length;
            offset += UNIT_SIZE;
        }
        try {
            end.await();
        } catch (InterruptedException e) {
            log.error("downLoad await error.", e);
        }
        log.info("download file done！localPath: {}, size: {} KB, time: {} S", localPath, fileSizeResult, (System.currentTimeMillis() - start) / 1000);
        return fileSizeResult;
    }

    /**
     * 下载文件，并在下载完成后，校验MD5值是否正确；如果MD5校验未通过则删除已下载的文件
     *
     * @param fileUrl   资源路径
     * @param localPath 文件路径
     * @param md5       文件的MD5
     */
    public static long download(String fileUrl, String localPath, String md5) throws Exception {
        long filesize = download(fileUrl, localPath);
        if (!FileUtil.checkFileMd5(localPath, md5)) {
            log.error("download file failed, because md5 check failed. file: {}, md5: {}", localPath, md5);
            FileUtil.deleteFile(localPath);
            return -1L;
        }
        log.info("download file check md5 success, file: {}, md5: {}", localPath, md5);
        return filesize;
    }

    /**
     * 文件下载任务
     */
    @Data
    @Accessors(chain = true)
    private static class DownLoadTask implements Runnable {
        /*** 待下载的文件 */
        private String url;
        /*** 本地文件名 */
        private String fileName;
        /*** 偏移量 */
        private Long offset;
        /*** 分配给本线程的下载字节数 */
        private Long length;

        private CountDownLatch end;
        private HttpContext context;

        @Override
        @SneakyThrows
        public void run() {
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Range", "bytes=" + offset + "-" + (offset + length - 1));
            File file = new File(fileName);
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
                 CloseableHttpResponse response = HttpUtil.HTTP_CLIENT.execute(httpGet, context);
                 BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent())) {
                int read;
                byte[] bytes = new byte[Contents.DEFAULT_SIZE];
                while ((read = bis.read(bytes, 0, bytes.length)) != -1) {
                    raf.seek(offset);
                    raf.write(bytes, 0, read);
                    offset += read;
                }
            } finally {
                end.countDown();
                log.info("task: {} is go on!", end.getCount());
            }
        }

        static DownLoadTask getInstance(String url, String fileName, long offset, long length, CountDownLatch end) {
            return new DownLoadTask().setUrl(url).setFileName(fileName).setOffset(offset)
                    .setLength(length).setEnd(end).setContext(new BasicHttpContext());
        }

        private DownLoadTask() {
        }
    }
}