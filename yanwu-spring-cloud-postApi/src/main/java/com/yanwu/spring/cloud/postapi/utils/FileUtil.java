package com.yanwu.spring.cloud.postapi.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * @author XuBaofeng.
 * @date 2018/6/12.
 */
@Slf4j
@SuppressWarnings("unused")
public class FileUtil {

    /*** 当文件大于5M时，进行限速下载 ***/
    public static final Long LIMIT_SIZE = 5 * 1024 * 1024L;
    /*** 限速下载时的速度：1M/S ***/
    public static final Long SPEED = 1024 * 1024L;
    /*** 限速下载时每次写出的大小：1M ***/
    public static final Integer SIZE = 1024 * 1024;

    private FileUtil() {
        throw new UnsupportedOperationException("FileUtil should never be instantiated");
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
        if (!fileExists(delFile)) {
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
        if (!fileExists(dirFile) || !dirFile.isDirectory()) {
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
     * 读取文件
     *
     * @param path 文件路径
     * @return 文件内容
     */
    public static byte[] read(String path) throws Exception {
        return read(path, 0, (int) (new File(path)).length());
    }

    /**
     * 切片读取文件块
     *
     * @param path      文件路径
     * @param position  角标
     * @param blockSize 文件块大小
     * @return 文件块内容
     */
    public static byte[] read(String path, long position, int blockSize) throws Exception {
        // ----- 校验文件，当文件不存在时，抛出文件不存在异常
        if (!fileExists(path)) {
            return null;
        }
        // ----- 读取文件
        ByteBuffer block = ByteBuffer.allocate(blockSize);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
            Future<Integer> read = channel.read(block, position);
            while (!read.isDone()) {
                ThreadUtil.sleep(10);
            }
        }
        return block.array();
    }

    /**
     * 覆盖文件内容
     *
     * @param filepath 文件路径
     * @param block    新内容
     */
    public static boolean resetFile(String filepath, byte[] block) {
        return resetFile(new File(filepath), block);
    }

    /**
     * 覆盖文件内容
     *
     * @param file  文件
     * @param block 新内容
     */
    public static boolean resetFile(File file, byte[] block) {
        if (!fileExists(file) || !file.isFile()) {
            log.error("file reset failed, because file is not exists.");
            return false;
        }
        String newFilepath = file.getParentFile().getPath() + File.separator + file.getName() + "_backup";
        if (!rename(file, newFilepath)) {
            log.error("file reset failed, because file backup failed.");
            return false;
        }
        try {
            appendWrite(file.getPath(), block);
            deleteFile(newFilepath);
            return true;
        } catch (Exception e) {
            log.error("file reset failed. reply to the source file.", e);
            deleteFile(file.getPath());
            rename(newFilepath, file.getName());
            return false;
        }
    }

    /**
     * 文件重命名
     *
     * @param filepath 文件全路径
     * @param newName  新文件名
     */
    @SuppressWarnings("all")
    public static boolean rename(String filepath, String newName) {
        return rename(new File(filepath), newName);
    }

    /**
     * 文件重命名
     *
     * @param file    文件
     * @param newName 新文件名
     */
    public static boolean rename(File file, String newName) {
        return rename(file, file.getParentFile().getPath(), newName);
    }

    /**
     * 文件重命名
     *
     * @param filepath    文件全路径
     * @param newFilepath 新文件目录
     * @param newName     新文件名
     */
    public static boolean rename(String filepath, String newFilepath, String newName) {
        return rename(new File(filepath), newFilepath, newName);
    }

    /**
     * 文件重命名
     *
     * @param file        文件
     * @param newFilepath 新文件目录
     * @param newName     新文件名
     */
    public static boolean rename(File file, String newFilepath, String newName) {
        if (!fileExists(file) || !file.isFile()) {
            log.error("file rename failed, because file is not exists.");
            return false;
        }
        File newFile = new File(newFilepath + File.separator + newName);
        return file.renameTo(newFile);
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
        if (!fileExists(path)) {
            File file = new File(path);
            if (!fileExists(file.getParentFile())) {
                Files.createDirectories(file.getParentFile().toPath());
            }
            Files.createFile(file.toPath());
        }
        // ----- 写文件
        ByteBuffer buffer = ByteBuffer.wrap(block);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(path), StandardOpenOption.WRITE)) {
            Future<Integer> write = channel.write(buffer, position);
            while (!write.isDone()) {
                ThreadUtil.sleep(10);
            }
        }
    }

    /**
     * 将内容追加到指定的文件
     *
     * @param filePath 文件路径
     * @param block    内容
     * @throws Exception e
     */
    public static void appendWrite(String filePath, byte[] block) throws Exception {
        write(filePath, block, new File(filePath).length());
    }

    /**
     * 根据文件名和所给的目录，找到文件所在绝对路径
     *
     * @param dirPath  目录路径
     * @param filename 文件名
     * @return 文件所在绝对路径
     */
    public static String findFilepath(String dirPath, String filename) {
        if (StringUtils.isBlank(dirPath) || StringUtils.isBlank(filename)) {
            return null;
        }
        return findFilepath(new File(dirPath), filename);
    }

    /**
     * 根据文件名和所给的目录，找到文件所在绝对路径
     *
     * @param file     目录路径
     * @param filename 文件名
     * @return 文件所在绝对路径
     */
    public static String findFilepath(File file, String filename) {
        if (!fileExists(file) || StringUtils.isBlank(filename)) {
            return null;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File item : files) {
                    if (item.isDirectory()) {
                        String filepath = findFilepath(item, filename);
                        if (StringUtils.isNotBlank(filepath)) {
                            return filepath;
                        }
                    } else {
                        if (item.getName().equalsIgnoreCase(filename)) {
                            return item.getPath();
                        }
                    }
                }
            }
        } else {
            if (file.getName().equalsIgnoreCase(filename)) {
                return file.getPath();
            }
        }
        return null;
    }

    /**
     * 根据文件名和所给的目录，模糊搜索所有文件名包含filename的文件所在绝对路径集合
     *
     * @param dirPath  目录路径
     * @param filename 文件名
     * @return 文件所在绝对路径集合
     */
    public static Set<String> fuzzyFindFilepath(String dirPath, String filename) {
        if (StringUtils.isBlank(dirPath) || StringUtils.isBlank(filename)) {
            return Collections.emptySet();
        }
        return fuzzyFindFilepath(new File(dirPath), filename);
    }

    /**
     * 根据文件名和所给的目录，模糊搜索所有文件名包含filename的文件所在绝对路径集合
     *
     * @param file     目录路径
     * @param filename 文件名
     * @return 文件所在绝对路径集合
     */
    public static Set<String> fuzzyFindFilepath(File file, String filename) {
        if (!fileExists(file) || StringUtils.isBlank(filename)) {
            return Collections.emptySet();
        }
        Set<String> result = new HashSet<>();
        fuzzyFindFilepath(file, filename.toLowerCase(), result);
        return result;
    }

    /**
     * 根据文件名和所给的目录，模糊搜索所有文件名包含filename的文件所在绝对路径集合
     *
     * @param file     目录路径
     * @param filename 文件名
     * @param result   符合条件的文件路径集合
     */
    private static void fuzzyFindFilepath(File file, String filename, Set<String> result) {
        if (!fileExists(file) || StringUtils.isBlank(filename)) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File item : files) {
                    if (item.isDirectory()) {
                        fuzzyFindFilepath(item, filename, result);
                    } else {
                        if (item.getName().contains(filename)) {
                            result.add(item.getPath());
                        }
                    }
                }
            }
        } else {
            if (file.getName().contains(filename)) {
                result.add(file.getPath());
            }
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param filepath 文件路径
     */
    public static boolean fileExists(String filepath) {
        return StringUtils.isNotBlank(filepath) && fileExists(new File(filepath));
    }

    /**
     * 检查文件是否存在
     *
     * @param file 文件
     */
    public static boolean fileExists(File file) {
        return file != null && file.exists();
    }

}
