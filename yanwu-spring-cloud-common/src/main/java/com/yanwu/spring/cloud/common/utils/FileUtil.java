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
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
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
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFile))) {
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
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile), Contents.DEFAULT_SIZE)) {
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
                 OutputStream os = new BufferedOutputStream(new FileOutputStream(targetFile))) {
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
        if (file.isFile()) {
            // ----- 下载文件
            log.info("export file: {}", file.getPath());
            return file.length() < LIMIT_SIZE ? exportSmallFile(file) : exportBigFile(file, response);
        } else if (file.isDirectory()) {
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
                // ----- 睡50毫秒， 不抢占资源
                ThreadUtil.sleep(50);
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
                // ----- 睡50毫秒， 不抢占资源
                ThreadUtil.sleep(50);
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

}
