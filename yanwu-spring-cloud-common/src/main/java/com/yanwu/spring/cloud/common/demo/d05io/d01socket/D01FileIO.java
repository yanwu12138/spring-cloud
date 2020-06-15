package com.yanwu.spring.cloud.common.demo.d05io.d01socket;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/15 9:18.
 * <p>
 * description:
 */
@Slf4j
public class D01FileIO {

    private static final byte[] DATA = "1234567890\n".getBytes();
    private static final String PATH = "F:\\file\\";
    private static final Integer SIZE = 100_000;

    public static void main(String[] args) throws Exception {
        basicFileIo();
        bufferFileIo();
        randomAccessFileIo();
    }

    /**
     * 简单的最基本的BIO文件读写
     *
     * @throws Exception e
     */
    public static void basicFileIo() throws Exception {
        File file = new File(getPath());
        if (file.createNewFile()) {
            try (OutputStream os = new FileOutputStream(file)) {
                long beginTime = System.nanoTime();
                int len = SIZE;
                while ((len--) > 0) {
                    os.write(DATA);
                }
                long endTime = System.nanoTime();
                log.info("----- basic file io write file time: {}", (endTime - beginTime));
            }
        }
    }

    /**
     * 使用BIO的buffer读写文件
     *
     * @throws Exception e
     */
    public static void bufferFileIo() throws Exception {
        File file = new File(getPath());
        if (file.createNewFile()) {
            try (OutputStream os = new BufferedOutputStream(new FileOutputStream(file))) {
                long beginTime = System.nanoTime();
                int len = SIZE;
                while ((len--) > 0) {
                    os.write(DATA);
                }
                long endTime = System.nanoTime();
                log.info("----- buffer file io write file time: {}", (endTime - beginTime));
            }
        }
    }

    public static void randomAccessFileIo() throws Exception {
        RandomAccessFile raf = new RandomAccessFile(getPath(), "rw");
        // ----- 对文件进行写操作
        raf.write("hello yanwu\n".getBytes());
        raf.write("hello locus\n".getBytes());
        System.out.println("----- write -----");
//        System.in.read();
        // ----- 修改偏移，然后再对文件进行写操作，观察文件
        raf.seek(3);
        raf.write("wenxin".getBytes());
        System.out.println("----- seek -----");
//        System.in.read();
        // ----- 使用堆外内存
        FileChannel channel = raf.getChannel();
        MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_WRITE, 0, 4096);
        mbb.put("wenfu".getBytes());
        System.out.println("----- mbb put -----");
//        System.in.read();
        raf.seek(0);
        // ----- NIO写文件，使用堆外内存
        ByteBuffer bb = ByteBuffer.allocateDirect(8192);
        channel.read(bb);
        log.info("byte buffer: {}", bb);
        bb.flip();
        log.info("byte buffer: {}", bb);
        bb.compact();
        log.info("byte buffer: {}", bb);
        long beginTime = System.nanoTime();
        int len = SIZE;
        while ((len--) > 0) {
            bb.put(DATA);
            if (bb.position() >= bb.capacity()) {
                channel.write(bb);
                bb.clear();
            }
        }
        long endTime = System.nanoTime();
        log.info("----- basic file io write file time: {}", (endTime - beginTime));
    }


    private static String getPath() {
        String filename = LocalDateTime.now().toString();
        filename = filename.replaceAll("-", "");
        filename = filename.replaceAll(":", "");
        filename = filename.replaceAll("\\.", "");
        return PATH + filename.toUpperCase() + ".txt";
    }
}
