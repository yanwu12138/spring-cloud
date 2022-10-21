package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.Contents;
import com.yanwu.spring.cloud.common.core.common.Encoding;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author XuBaofeng.
 * @date 2018/6/12.
 */
@Slf4j
@SuppressWarnings("unused")
public class FileUtil {

    /*** 当文件大于5M时，进行限速下载 ***/
    private static final Long LIMIT_SIZE = 5 * 1024 * 1024L;
    /*** 限速下载时的速度：1M/S ***/
    private static final Long SPEED = 1024 * 1024L;
    /*** 限速下载时每次写出的大小：1M ***/
    private static final Integer SIZE = 1024 * 1024;

    private FileUtil() {
        throw new UnsupportedOperationException("FileUtil should never be instantiated");
    }

    /**
     * 根据文件地址获取文件大小
     *
     * @param filePath 本地文件地址
     * @return [-1: 未获取到文件大小]
     */
    private static long localFileSize(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return -1;
        }
        File file = new File(filePath);
        return file.isFile() ? file.length() : -1;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(remoteFileSize("https://bird-test-1.oss-cn-beijing.aliyuncs.com/test-0.0.0.34.bin"));
    }

    /**
     * 根据文件地址获取文件大小
     * - 该方法可能存在误差
     *
     * @param fileUrl 远程文件地址
     * @return [-1: 未获取到文件大小]
     */
    public static long remoteFileSize(String fileUrl) throws Exception {
        return StringUtils.isBlank(fileUrl) ? -1 : remoteFileSize(new URL(fileUrl));
    }

    /**
     * 根据文件地址获取文件大小
     * - 该方法可能存在误差
     *
     * @param fileUrl 远程文件地址
     * @return [-1: 未获取到文件大小]
     */
    public static long remoteFileSize(URL fileUrl) throws Exception {
        if (fileUrl == null) {
            return -1;
        }
        URLConnection conn = null;
        try {
            conn = fileUrl.openConnection();
            return conn.getContentLength();
        } catch (Exception e) {
            return -1;
        } finally {
            if (conn != null) {
                IOUtil.close(conn.getInputStream());
                IOUtil.close(conn.getOutputStream());
            }
        }
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
        targetDir = targetDir.endsWith(File.separator) ? targetDir : targetDir + File.separator;
        // ----- 检查资源：资源目录是否存在 && 资源目录是否为空
        File sourceFile = new File(sourceDir);
        Assert.isTrue((sourceFile.exists() && sourceFile.isDirectory()), sourceDir + " >> is not exists");
        File[] sourceFiles = sourceFile.listFiles();
        Assert.isTrue((sourceFiles != null && sourceFiles.length > 0), sourceDir + " >> directory is empty");
        // ----- 检查目标：目标目录是否存在[不存在进行创建] && 目标文件是否存在[存在进行删除]
        checkDirectoryPath(targetDir);
        fileName = StringUtils.isNotBlank(fileName) ? fileName : sourceFile.getName() + FileType.ZIP.getSuffix();
        File targetFile = new File(targetDir + fileName);
        if (!targetFile.exists() || targetFile.delete()) {
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(targetFile.toPath()))) {
                // ----- 压缩
                toZip(zos, sourceFile, Contents.NUL);
            }
        }
        log.info("file to zip, source: {}, file: {}, target: {}", sourceDir, fileName, targetDir);
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
            byte[] bytes = new byte[Contents.DEFAULT_SIZE];
            zos.putNextEntry(new ZipEntry(directory));
            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(sourceFile.toPath()), Contents.DEFAULT_SIZE)) {
                int read;
                while ((read = bis.read(bytes, 0, Contents.DEFAULT_SIZE)) != -1) {
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
            directory = directory.length() == 0 ? Contents.NUL : directory + File.separator;
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
        targetDir = targetDir + sourceName.substring(0, sourceName.lastIndexOf(Contents.POINT));
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
        log.info("file un zip, filePath: {}, target: {}", filePath, targetDir);
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
            try (InputStream is = new BufferedInputStream(zipFile.getInputStream(zipEntry));
                 OutputStream os = new BufferedOutputStream(Files.newOutputStream(targetFile.toPath()))) {
                int read;
                byte[] bytes = new byte[Contents.DEFAULT_SIZE];
                while ((read = is.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
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
     * 获取文件的后缀名，如：
     * * aaa.zip        >> zip
     * * aaa.tar.gz     >> gz
     *
     * @param filename 文件名
     * @return 后缀名
     */
    public static String getSuffix(String filename) {
        if (StringUtils.isBlank(filename)) {
            return null;
        }
        return filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
    }

    /**
     * 获取文件的前缀名，如：
     * * aaa.txt        >> aaa
     * * aaa.123.txt    >> aaa
     *
     * @param filename 文件名
     * @return 前缀名
     */
    public static String getPrefix(String filename) {
        if (StringUtils.isBlank(filename)) {
            return null;
        }
        return filename.contains(".") ? filename.substring(0, filename.indexOf(".")) : filename;
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
     * @param response 输出流
     * @return 响应
     * @throws Exception e
     */
    public static ResponseEntity<Resource> exportFile(String filePath, HttpServletResponse response) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            log.error("export error, filePath is blank");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("export error, file is not exists, file: {}", file.getPath());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        if (fileAttributes.isRegularFile()) {
            // ----- 下载文件
            log.info("export file: {}", file.getPath());
            return fileAttributes.size() < LIMIT_SIZE ? exportSmallFile(file) : exportBigFile(file, response);
        } else if (fileAttributes.isDirectory()) {
            // ----- 下载文件夹
            log.info("export directory: {}", file.getPath());
            return exportDirectory(file, response);
        } else {
            // ----- 下载错误，既不是目录也不是文件
            log.error("export error, {} it is neither a file nor a directory", file.getPath());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 下载小文件 [ size < 5M ]，不限速下载
     *
     * @param file 文件
     * @return 响应
     * @throws Exception e
     */
    private static ResponseEntity<Resource> exportSmallFile(File file) throws Exception {
        if (file.length() >= LIMIT_SIZE) {
            log.info("export file error, file size exceed 5M, file: {}, size: {}", file.getPath(), file.length());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        String fileDisposition = "attachment;filename=" + URLEncoder.encode(file.getName(), Encoding.UTF_8);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .header(HttpHeaders.CONTENT_ENCODING, Encoding.UTF_8)
                .header(HttpHeaders.CONTENT_DISPOSITION, fileDisposition)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                .body(new ByteArrayResource(Files.readAllBytes(file.toPath())));
    }

    /**
     * 下载大文件 [ size >= 5M ]，限速下载
     *
     * @param file     文件
     * @param response 输出流
     * @return 响应
     * @throws Exception e
     */
    private static ResponseEntity<Resource> exportBigFile(File file, HttpServletResponse response) throws Exception {
        // ----- 响应状态
        String fileDisposition = "attachment;filename=" + URLEncoder.encode(file.getName(), Encoding.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        response.setHeader(HttpHeaders.CONTENT_ENCODING, Encoding.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, fileDisposition);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()));
        // ----- 使用输出流往客户端写出文件
        long position = 0, length = file.length();
        try (OutputStream outputStream = response.getOutputStream()) {
            while (position < length) {
                int blockSize = (int) Math.min(SIZE, length - position);
                byte[] bytes = read(file.getPath(), position, blockSize);
                outputStream.write(bytes);
                outputStream.flush();
                if (SPEED < 1000L * bytes.length) {
                    ThreadUtil.sleep(Math.floorDiv(1000L * bytes.length, SPEED));
                }
                position += blockSize;
            }
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 下载文件夹
     *
     * @param file     文件夹
     * @param response 输出流
     * @return 响应
     * @throws Exception e
     */
    private static ResponseEntity<Resource> exportDirectory(File file, HttpServletResponse response) throws Exception {
        // ----- 压缩文件夹
        toZip(file.getPath(), file.getParent(), file.getName() + FileType.ZIP.getSuffix());
        File zipFile = new File(file.getPath() + FileType.ZIP.getSuffix());
        try {
            // ----- 下载压缩后的文件
            return exportBigFile(zipFile, response);
        } finally {
            // ----- 下载完成后删除压缩包
            deleteFile(zipFile);
        }
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
        checkFilePath(path, Boolean.FALSE);
        // ----- 读取文件
        ByteBuffer block = ByteBuffer.allocate(blockSize);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
            Future<Integer> read = channel.read(block, position);
            while (!read.isDone()) {
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
        if (!file.exists() || !file.isFile()) {
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
        if (!file.exists() || !file.isFile()) {
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
        checkFilePath(path, Boolean.TRUE);
        // ----- 写文件
        ByteBuffer buffer = ByteBuffer.wrap(block);
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get(path), StandardOpenOption.WRITE)) {
            Future<Integer> write = channel.write(buffer, position);
            while (!write.isDone()) {
            }
        }
    }

    /**
     * 将文件写入到本地磁盘
     *
     * @param is       输入流
     * @param filePath 文件磁盘地址
     * @throws Exception e
     */
    public static void write(InputStream is, String filePath) throws Exception {
        checkFilePath(filePath, Boolean.TRUE);
        int position = 0, available = is.available();
        while (available > 0) {
            byte[] bytes = new byte[Math.min(SIZE, available)];
            int read = is.read(bytes);
            write(filePath, bytes, position);
            position += bytes.length;
            available = is.available();
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
     * 对文件按照指定的大小进行切片
     *
     * @param file        文件
     * @param magic       切片的文件名
     * @param sliceLength 每片的大小：例如文件有100M,每个切片(sliceLength)为1M,就有100个切片
     * @return 切片数
     */
    public static long slice(File file, Long magic, int sliceLength) throws Exception {
        if (!file.exists() || file.isDirectory()) {
            return 0;
        }
        deleteFile((file.getParent() + File.separator + getPrefix(file.getName())));
        // ===== 开始切片
        try (FileInputStream fis = new FileInputStream(file);
             FileChannel channel = fis.getChannel()) {
            long totalSize = channel.size(), offset = 0, fragment = 0;
            while (offset < totalSize) {
                int length = (int) Math.min(totalSize - offset, sliceLength);
                byte[] slice = read(file.getPath(), offset, length);
                write(getTargetFile(file, magic, fragment), slice, 0);
                offset += sliceLength;
                fragment++;
            }
            return (int) (totalSize % sliceLength == 0 ? totalSize / sliceLength : totalSize / sliceLength + 1);
        }
    }

    /**
     * 根据相关条件获取切片的路径
     *
     * @param file   文件
     * @param magic  切片前缀
     * @param seqNum 切片编号
     * @return 切片路径
     */
    private static String getTargetFile(File file, long magic, long seqNum) {
        return file.getParent() + File.separator + getPrefix(file.getName()) + File.separator + magic + "_" + seqNum + ".slice";
    }

    /**
     * 将多个文件合并成一个文件
     *
     * @param newFile  最终的文件
     * @param oldFiles 需要合并的文件集合
     */
    private static String merge(File newFile, File... oldFiles) throws Exception {
        if (Objects.isNull(newFile) || oldFiles == null || oldFiles.length == 0) {
            return null;
        }
        checkFilePath(newFile, true);
        String filePath = newFile.getPath();
        for (File oldFile : oldFiles) {
            BasicFileAttributes fileAttributes = Files.readAttributes(oldFile.toPath(), BasicFileAttributes.class);
            if (fileAttributes.isRegularFile()) {
                long position = 0, length = fileAttributes.size();
                while (position < length) {
                    int blockSize = (int) Math.min(SIZE, length - position);
                    appendWrite(filePath, read(oldFile.getPath(), position, blockSize));
                    position += blockSize;
                }
                deleteFile(oldFile);
            }
        }
        return filePath;
    }

    /**
     * 将切片后的文件合并成一个完整的文件
     *
     * @param sliceDir 切片目录
     * @return 合并后的文件路径
     */
    public static String merge(File sliceDir) throws Exception {
        if (!sliceDir.exists() || sliceDir.isFile()) {
            return sliceDir.getPath();
        }
        File[] files = sliceDir.listFiles();
        if (ArrayUtil.isEmpty(files)) {
            return null;
        }
        String target = sliceDir.getParent() + File.separator + System.currentTimeMillis();
        deleteFile(target);
        // ===== 开始合并
        long position = 0, index = 0, totalSize = files.length;
        while (index < totalSize) {
            File item = getSliceFile(files[0], index);
            if (item == null) {
                continue;
            }
            int length = (int) item.length();
            byte[] read = read(item.getPath(), 0, length);
            write(target, read, position);
            position += length;
            index++;
        }
        return target;
    }

    /**
     * 获取切片文件
     */
    private static File getSliceFile(File file, long index) {
        if (!file.exists() || index < 0) {
            return null;
        }
        String filename = file.getName();
        return new File(file.getParent() + File.separator + filename.substring(0, filename.indexOf("_")) + "_" + index + ".slice");
    }

    /**
     * 校验文件
     *
     * @param path 文件路径
     * @param flag 当文件不存在时，是创建文件还是抛出异常[true: 创建; false: 抛出异常]
     * @throws Exception e
     */
    public static void checkFilePath(String path, Boolean flag) throws Exception {
        Assert.isTrue(StringUtils.isNotBlank(path), "The file path cannot be empty.");
        checkFilePath(new File(path), flag);
    }

    /**
     * 校验文件
     *
     * @param file 文件
     * @param flag 当文件不存在时，是创建文件还是抛出异常[true: 创建; false: 抛出异常]
     * @throws Exception e
     */
    public static void checkFilePath(File file, Boolean flag) throws Exception {
        if (file.exists()) {
            return;
        }
        // ----- 当文件不存在时，是创建文件还是抛出异常[true: 创建; false: 抛出异常]
        Assert.isTrue(flag, "File does not exist.");
        checkDirectoryPath(file.getParentFile());
        Assert.isTrue(file.createNewFile(), "File does not exist.");
    }

    /**
     * 校验文件的MD5值
     *
     * @param filepath 文件全路径
     * @param md5      MD5值
     * @return 校验结果【true: 校验通过; false: 校验不通过】
     */
    public static boolean checkFileMd5(String filepath, String md5) throws Exception {
        if (StringUtils.isBlank(filepath)) {
            log.error("check file md5 failed, because file path is empty.");
            return false;
        }
        if (StringUtils.isBlank(md5)) {
            log.error("check file md5 failed, because md5 is empty.");
            return false;
        }
        String fileMd5 = calcFileMd5(filepath);
        return StringUtils.isNotBlank(fileMd5) && fileMd5.equals(md5.toUpperCase());
    }

    /**
     * 校验文件的MD5值
     *
     * @param file 文件
     * @param md5  MD5值
     * @return 校验结果【true: 校验通过; false: 校验不通过】
     */
    public static boolean checkFileMd5(File file, String md5) throws Exception {
        if (!file.exists() || !file.isFile()) {
            log.error("get file md5 failed, because file is empty.");
            return false;
        }
        if (StringUtils.isBlank(md5)) {
            log.error("check file md5 failed, because md5 is empty.");
            return false;
        }
        String fileMd5 = calcFileMd5(file);
        return StringUtils.isNotBlank(fileMd5) && fileMd5.equals(md5.toUpperCase());
    }

    /**
     * 计算文件的MD5值
     *
     * @param filepath 文件全路径
     * @return 文件的MD5值
     */
    public static String calcFileMd5(String filepath) throws Exception {
        return StringUtils.isBlank(filepath) ? null : calcFileMd5(new File(filepath));
    }

    /**
     * 计算文件的MD5值
     *
     * @param file 文件
     * @return 文件的MD5值
     */
    public static String calcFileMd5(File file) throws Exception {
        BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        if (!file.exists() || !fileAttributes.isRegularFile()) {
            log.error("calc file md5 failed, because file does not exist or is not a file.");
            return null;
        }
        MessageDigest digest = MessageDigest.getInstance("MD5");
        long length = fileAttributes.size(), position = 0, blockSize = LIMIT_SIZE;
        while (length > 0) {
            blockSize = Math.min(blockSize, length);
            digest.update(read(file.getPath(), position, (int) blockSize));
            position += blockSize;
            length -= blockSize;
        }
        String result = ByteUtil.bytesToHexStr(digest.digest());
        log.info("calc file md5 success, file: {}, md5: {}", file.getPath(), result);
        return result;
    }

    /**
     * 获取文件的创建时间
     *
     * @param filePath 文件路径
     * @return 创建时间
     */
    public static long createTime(String filePath) {
        return StringUtils.isBlank(filePath) ? -1 : createTime(new File(filePath));
    }

    /**
     * 获取文件的创建时间
     *
     * @param file 文件
     * @return 创建时间
     */
    public static long createTime(File file) {
        return file == null ? -1 : createTime(file.toPath());
    }

    /**
     * 获取文件的创建时间
     *
     * @param path 文件
     * @return 创建时间
     */
    public static long createTime(java.nio.file.Path path) {
        if (path == null) {
            return -1;
        }
        try {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return attributes.creationTime().toMillis();
        } catch (Exception e) {
            log.error("get file create time failed, file: {}.", path, e);
            return -1;
        }
    }

}
