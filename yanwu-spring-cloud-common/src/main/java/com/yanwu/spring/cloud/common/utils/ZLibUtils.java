package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
@SuppressWarnings("unused")
public class ZLibUtils {

    private ZLibUtils() {
        throw new UnsupportedOperationException("ZLibUtils should never be instantiated");
    }

    /*** 压缩字节数组 ***/
    public static byte[] compress(byte[] data) {
        byte[] result;
        Deflater compress = new Deflater();
        compress.reset();
        compress.setInput(data);
        compress.finish();
        byte[] tmp = new byte[1024];
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length)) {
            while (!compress.finished()) {
                int count = compress.deflate(tmp);
                bos.write(tmp, 0, count);
            }
            result = bos.toByteArray();
        } catch (Exception e) {
            result = data;
            log.error("zlib compress bytes failed. date: {}", ByteUtil.printBytes(data), e);
        } finally {
            compress.end();
        }
        return result;
    }

    /*** 解压缩字节数组 ***/
    public static byte[] decompress(byte[] data) {
        byte[] result;
        Inflater inflater = new Inflater();
        inflater.reset();
        inflater.setInput(data);
        byte[] tmp = new byte[1024];
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length)) {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                bos.write(tmp, 0, count);
            }
            result = bos.toByteArray();
        } catch (Exception e) {
            result = data;
            log.error("zlib decompress bytes failed. date: {}", ByteUtil.printBytes(data), e);
        } finally {
            inflater.end();
        }
        return result;
    }

}