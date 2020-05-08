package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.Constants;
import com.yanwu.spring.cloud.common.core.common.Encoding;
import com.yanwu.spring.cloud.common.core.exception.BusinessException;
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
import java.util.Enumeration;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author XuBaofeng.
 * @date 2018/6/12.
 */
@SuppressWarnings("unused")
public class FileUtil {

    public static void main(String[] args) throws Exception {
        String sourceDir = "F:\\document\\工作日志";
        String targetDir = "F:\\file\\2020\\";
        String fileName = "111.zip";
        toZip(sourceDir, targetDir, fileName);
        unZip(targetDir + fileName, targetDir);
    }

    /**
     * 将source目录下所有文件打包:
     * 名称为: fileName到target目录下
     *
     * @param sourceDir 资源路径
     * @param targetDir 目标路径
     * @param fileName  文件名
     * @throws Exception e
     */
    public static void toZip(String sourceDir, String targetDir, String fileName) throws Exception {
        // ----- 检查资源：资源目录是否存在 && 资源目录是否为空
        File sourceFile = new File(sourceDir);
        Assert.isTrue((sourceFile.exists() && sourceFile.isDirectory()), sourceDir + " >> is not exists");
        File[] sourceFiles = sourceFile.listFiles();
        Assert.isTrue((sourceFiles != null && sourceFiles.length > 0), sourceDir + " >> directory is empty");
        // ----- 检查目标：目标目录是否存在[不存在进行创建] && 目标文件是否存在[存在进行删除]
        checkDirectoryPath(targetDir);
        File targetFile = new File(targetDir + fileName);
        if (!targetFile.exists() || targetFile.delete()) {
            try (OutputStream fos = new FileOutputStream(targetFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                // ----- 压缩
                toZip(zos, sourceFile, Constants.NUL);
            }
        }
    }

    /**
     * 压缩
     *
     * @param zos        输出流
     * @param sourceFile 资源文件
     * @param directory  目录
     * @throws Exception e
     */
    private static void toZip(ZipOutputStream zos, File sourceFile, String directory) throws Exception {
        if (sourceFile.isFile()) {
            // ===== 文件，添加到压缩文件
            byte[] bytes = new byte[Constants.DEFAULT_SIZE];
            zos.putNextEntry(new ZipEntry(directory));
            try (FileInputStream fis = new FileInputStream(sourceFile);
                 BufferedInputStream bis = new BufferedInputStream(fis, Constants.DEFAULT_SIZE)) {
                int read;
                while ((read = bis.read(bytes, 0, Constants.DEFAULT_SIZE)) != -1) {
                    zos.write(bytes, 0, read);
                }
            }
        } else {
            // ===== 文件夹, 递归压缩
            File[] files = sourceFile.listFiles();
            zos.putNextEntry(new ZipEntry(directory + File.separator));
            if (files == null) {
                return;
            }
            directory = directory.length() == 0 ? Constants.NUL : directory + File.separator;
            for (File file : files) {
                toZip(zos, file, directory + file.getName());
            }
        }
    }

    /**
     * 解压缩
     *
     * @param filePath  资源文件
     * @param targetDir 解压目录
     * @throws Exception e
     */
    public static void unZip(String filePath, String targetDir) throws Exception {
        // ----- 检查资源：资源目录是否存在
        File sourceFile = new File(filePath);
        Assert.isTrue((sourceFile.exists() && sourceFile.isFile()), filePath + " >> is not exists");
        // ----- 检查目标：目标目录是否存在[存在：先删除后创建；不存在：直接创建]
        String sourceName = sourceFile.getName();
        targetDir = targetDir + sourceName.substring(0, sourceName.lastIndexOf(Constants.POINT));
        File targetFile = new File(targetDir);
        if (!targetFile.exists() || deleteFile(targetFile)) {
            checkDirectoryPath(targetFile);
        }
        // ----- 解压缩
        try (ZipFile zipFile = new ZipFile(sourceFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                unZip(zipFile, entries.nextElement(), targetDir);
            }
        }
    }

    /**
     * 解压缩
     *
     * @param zipFile   压缩文件
     * @param zipEntry  entry
     * @param targetDir 目标路径
     * @throws Exception e
     */
    private static void unZip(ZipFile zipFile, ZipEntry zipEntry, String targetDir) throws Exception {
        String entryName = zipEntry.getName();
        if (entryName.equals(File.separator)) {
            return;
        }
        String filePath = targetDir + File.separator + entryName;
        File targetFile = new File(filePath);
        if (entryName.endsWith(File.separator)) {
            // ----- 目录
            checkDirectoryPath(targetFile);
            return;
        }
        // ----- 文件
        if (targetFile.createNewFile()) {
            try (InputStream is = zipFile.getInputStream(zipEntry);
                 FileOutputStream fos = new FileOutputStream(targetFile)) {
                int read;
                byte[] bytes = new byte[Constants.DEFAULT_SIZE];
                while ((read = is.read(bytes)) != -1) {
                    fos.write(bytes, 0, read);
                }
            }
        }
    }


    /**
     * 删除文件
     *
     * @param fileName 要删除的文件的路径
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        return deleteFile(new File(fileName));
    }


    /**
     * 删除文件
     *
     * @param delFile 要删除的文件
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(File delFile) {
        if (!delFile.exists()) {
            return false;
        }
        if (delFile.isFile()) {
            // ----- 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
            return delFile.delete();
        } else if (delFile.isDirectory()) {
            // ----- 删除目录
            return deleteDirectory(delFile);
        }
        return false;
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dirFile 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(File dirFile) {
        // ===== 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // ===== 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        if (files == null) {
            return false;
        }
        for (File file : files) {
            // ===== 删除子文件
            if (file.isFile()) {
                flag = file.delete();
                if (!flag) {
                    break;
                }
            } else if (file.isDirectory()) {
                // ===== 删除子目录
                flag = deleteDirectory(file);
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        // ===== 删除当前目录
        return dirFile.delete();
    }

    /**
     * 检查目标目录是否存在，不存在时新建文件夹
     *
     * @param targetPath 目标目录
     * @return [true: 存在; false: 不存在]
     */
    public static boolean checkDirectoryPath(String targetPath) {
        return checkDirectoryPath(new File(targetPath));
    }

    /**
     * 检查目标目录是否存在，不存在时新建文件夹
     *
     * @param file 目标目录
     * @return [true: 存在; false: 不存在]
     */
    public static boolean checkDirectoryPath(File file) {
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 导出文件
     *
     * @param filePath 文件路径
     * @param fileName 文件名称
     * @return 输出流
     * @throws Exception e
     */
    public static ResponseEntity<Resource> exportFile(String filePath, String fileName) throws Exception {
        FileSystemResource file = new FileSystemResource(filePath);
        String fileDisposition = "attachment;filename=" + URLEncoder.encode(fileName, Encoding.UTF_8);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition")
                .header(HttpHeaders.CONTENT_ENCODING, Encoding.UTF_8)
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
        checkFilePath(path, Boolean.FALSE);
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
     * @throws Exception e
     */
    public static void write(String path, byte[] block, long position) throws Exception {
        // ----- 校验文件，当文件不存在时，创建新文件
        checkFilePath(path, Boolean.TRUE);
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
     * @param flag 当文件不存在时，是创建文件还是抛出异常[true: 创建; false: 抛出异常]
     * @throws Exception e
     */
    private static void checkFilePath(String path, Boolean flag) throws Exception {
        Assert.isTrue(StringUtils.isNotBlank(path), "The file path cannot be empty.");
        File file = new File(path);
        if (file.exists()) {
            return;
        }
        // ----- 当文件不存在时，是创建文件还是抛出异常[true: 创建; false: 抛出异常]
        if (flag) {
            Assert.isTrue(file.createNewFile(), "File does not exist.");
        } else {
            throw new BusinessException("File does not exist.");
        }
    }

}
