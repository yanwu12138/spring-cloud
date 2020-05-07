package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.enums.FileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author XuBaofeng.
 * @date 2018/6/12.
 */
@Slf4j
public class FileUtil {
    private static final String POINT = ".";
    private static final Integer DEFAULT_SIZE = 1024 * 10;

    /**
     * 将sourceFilePath目录下所有文件打包:
     * 名称为: fileName到zipFilePath目录下
     *
     * @param sourceFilePath
     * @param zipFilePath
     * @param fileName
     * @return
     * @throws Exception
     */
    public static void fileToZip(String sourceFilePath, String zipFilePath, String fileName) throws Exception {
        File sourceFile = new File(sourceFilePath);
        Assert.isTrue((!sourceFile.exists()), sourceFilePath + " >>>> is not exists");
        File file = new File(zipFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
        if (zipFile.exists()) {
            // ===== zip文件存在, 将原有文件删除
            zipFile.delete();
        }
        // ===== zip文件不存在, 打包
        pushZip(sourceFile, sourceFilePath, zipFile);
    }

    private static void pushZip(File sourceFile, String sourceFilePath, File zipFile) throws Exception {
        File[] sourceFiles = sourceFile.listFiles();
        if (sourceFiles == null || sourceFiles.length < 1) {
            log.info("{} >>>> is null", sourceFilePath);
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {
            byte[] bytes = new byte[DEFAULT_SIZE];
            for (File file : sourceFiles) {
                // ===== 创建ZIP实体，并添加进压缩包
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zos.putNextEntry(zipEntry);
                // ===== 读取待压缩的文件并写进压缩包里
                try (FileInputStream fis = new FileInputStream(file);
                     BufferedInputStream bis = new BufferedInputStream(fis, 10240)) {
                    int read = 0;
                    while ((read = bis.read(bytes, 0, DEFAULT_SIZE)) != -1) {
                        zos.write(bytes, 0, read);
                    }
                }
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // ===== 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (!file.exists() || !file.isFile()) {
            log.info("delete directory failed: directory >> [ {} ] non-existent!", fileName);
            return false;
        }
        boolean delete = file.delete();
        log.info("delete directory [ {} ] {}!", fileName, delete);
        return delete;
    }

    /**
     * 检查目标目录是否存在，不存在时新建文件夹
     *
     * @param targetPath 目标目录
     * @return [true: 存在; false: 不存在]
     */
    public static boolean checkTargetPath(String targetPath) {
        return checkTargetPath(new File(targetPath));
    }

    /**
     * 检查目标目录是否存在，不存在时新建文件夹
     *
     * @param file 目标目录
     * @return [true: 存在; false: 不存在]
     */
    public static boolean checkTargetPath(File file) {
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // ===== 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // ===== 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            log.info("delete directory failed: directory >> [ {} ] non-existent!", dir);
            return false;
        }
        boolean flag = true;
        // ===== 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (ArrayUtil.isEmpty(files)) {
            log.info("delete directory failed: directory >> [ {} ] non-existent!", dir);
            return false;
        }
        for (File file : files) {
            // ===== 删除子文件
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else if (file.isDirectory()) {
                // ===== 删除子目录
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            log.info("delete directory failed！");
            return false;
        }
        // ===== 删除当前目录
        boolean delete = dirFile.delete();
        log.info("delete directory [ {} ] {}", dir, delete);
        return delete;
    }

    public static FileType getFileTypeByName(String fileName) {
        Assert.isTrue(StringUtils.isNotBlank(fileName), "file name is empty.");
        if (fileName.contains(POINT)) {
            String suffix = fileName.substring(fileName.lastIndexOf(POINT));
            FileType fileType = FileType.getTypeBySuffix(suffix);
            return fileType != null ? fileType : FileType.OTHERS;
        }
        return FileType.OTHERS;
    }

    public static ResponseEntity<Resource> exportFile(String filePath, String fileName) throws Exception {
        FileSystemResource file = new FileSystemResource(filePath);
        String fileDisposition = "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8");
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                .header(HttpHeaders.CONTENT_ENCODING, "UTF-8")
                .header(HttpHeaders.CONTENT_DISPOSITION, fileDisposition)
                .body(new InputStreamResource(file.getInputStream()));
    }

    /**
     * 分片读取文件块
     *
     * @param path      文件路径
     * @param position  角标
     * @param blockSize 文件块大小
     * @return 文件块内容
     */
    public static byte[] read(String path, long position, int blockSize) throws Exception {
        // ----- 校验文件，当文件不存在时，抛出文件不存在异常
        checkFilePath(path);
        // ----- 读取文件
        ByteBuffer block = ByteBuffer.allocate(blockSize);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
            Future<Integer> read = channel.read(block, position);
            while (!read.isDone()) {
                // ----- 睡1毫秒， 不抢占资源
                Thread.sleep(1L);
            }
        }
        return block.array();
    }

    /**
     * 分片写文件
     *
     * @param path     文件目标位置
     * @param block    文件块内容
     * @param position 角标
     * @throws Exception
     */
    public static void write(String path, byte[] block, long position) throws Exception {
        // ----- 校验文件，当文件不存在时，创建新文件
        checkFilePath(path);
        // ----- 写文件
        ByteBuffer buffer = ByteBuffer.wrap(block);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(path), StandardOpenOption.WRITE)) {
            Future<Integer> write = channel.write(buffer, position);
            while (!write.isDone()) {
                // ----- 睡1毫秒， 不抢占资源
                Thread.sleep(1L);
            }
        }
    }

    /**
     * 校验文件
     *
     * @param path 文件路径
     * @throws Exception
     */
    private static void checkFilePath(String path) throws Exception {
        Assert.isTrue(StringUtils.isNotBlank(path), "The file path cannot be empty.");
        File file = new File(path);
        Assert.isTrue((file.exists() && file.isFile()), "File does not exist.");
    }

}
