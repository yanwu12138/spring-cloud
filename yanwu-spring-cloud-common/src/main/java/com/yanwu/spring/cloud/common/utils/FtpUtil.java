package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.common.Encoding;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.util.Assert;

import java.io.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-26 14:22.
 * <p/>
 * description: 简单操作FTP工具类
 */
@Slf4j
@SuppressWarnings("all")
public class FtpUtil {

    private static final String DEFAULT_PATH = "file";
    private static final String SEPARATOR = "/";

    private static final String FTP_HOST = "192.168.1.158";
    private static final Integer FTP_PORT = 21;
    private static final String USERNAME = "hoolink";
    private static final String PASSWORD = "hoolink123";

    /**
     * 初始化FTP
     *
     * @param host     ftp服务器地址
     * @param port     ftp端口
     * @param username ftp用户
     * @param password ftp密码
     */
    private static FTPClient initClient(String host, Integer port, String username, String password) {
        Assert.isTrue(StringUtils.isNotBlank(host), "init ftp client failed, host is null.");
        // ----- 当端口为空时使用默认端口
        int code = -1;
        port = port == null ? FTP_PORT : port;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.setControlEncoding(Encoding.UTF_8);
            // ----- 连接
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            // ----- 检测连接是否成功
            code = ftpClient.getReplyCode();
            if (FTPReply.isPositiveCompletion(code)) {
                log.info(" ----- init ftp server success host: {}, post: {}, user: {}, code: {}", host, port, username, code);
                return ftpClient;
            }
            log.error(" ----- init ftp server success host: {}, post: {}, user: {}, code: {}", host, port, username, code);
        } catch (Exception e) {
            log.error(" ----- init ftp server success host: {}, post: {}, user: {}, code: {}", host, port, username, code, e);
        }
        close(ftpClient);
        return null;
    }

    /**
     * 上传文件
     *
     * @param host       ftp服务器地址
     * @param port       ftp端口
     * @param username   ftp用户
     * @param password   ftp密码
     * @param file       文件
     * @param userId     用户ID
     * @param targetPath 文件存放目录
     * @return 文件存放在ftp服务器地址
     */
    public static String upload(String host, Integer port, String username, String password, File file, Long userId, String targetPath) {
        FTPClient ftpClient = initClient(host, port, username, password);
        if (Objects.isNull(ftpClient) || Objects.isNull(file) || !file.isFile()) {
            return null;
        }
        String fileName = file.getName();
        String filePath = getFilePath(userId, targetPath);
        try (InputStream is = new FileInputStream(file)) {
            StringBuilder sb = new StringBuilder();
            // ----- 切换到对应目录
            changeDirectory(ftpClient, filePath.split(SEPARATOR));
            // ----- 设置ftp对应的配置
            ftpClient.setBufferSize(1024);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // ----- 上传
            if (ftpClient.storeFile(fileName, is)) {
                sb.append(filePath).append(SEPARATOR).append(fileName);
                log.info(" ----- upload file success, filePath: {}", sb.toString());
                return sb.toString();
            }
            log.error(" ----- upload file failed, userId: {}, file: {}", userId, fileName);
        } catch (Exception e) {
            log.error(" ----- upload file failed, userId: {}, file: {}", userId, fileName, e);
        } finally {
            close(ftpClient);
        }
        return null;
    }

    /**
     * 断点续传FTP
     *
     * @param host       服务
     * @param port       端口
     * @param username   用户名
     * @param password   密码
     * @param is         文件流
     * @param projectId  项目ID
     * @param targetPath 目标地址
     * @param fileName   文件名
     * @param partNum    块数
     * @param ftpUrl     地址
     * @return 文件路径
     */
    public static String uploadPart(String host, Integer port, String username, String password,
                                    InputStream is, Long projectId, String targetPath, String fileName, Integer partNum, String ftpUrl) {
        FTPClient ftpClient = initClient(host, port, username, password);
        if (Objects.isNull(ftpClient) || Objects.isNull(is)) {
            return null;
        }
        String filePath = ftpUrl == null ? getFilePath(projectId, targetPath) : ftpUrl.substring(0, ftpUrl.lastIndexOf(SEPARATOR));
        StringBuilder sb = new StringBuilder(filePath).append(SEPARATOR).append(fileName);
        try {
            // ----- 切换到对应目录
            changeDirectory(ftpClient, filePath.split(SEPARATOR));
            ftpClient.setBufferSize(1024);
            ftpClient.enterLocalPassiveMode();
            // ----- 设置文件类型
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // ----- 第一次上传
            if (partNum == 0) {
                if (ftpClient.storeFile(fileName, is)) {
                    log.info(" ----- upload part file success, projectId: {}, file: {}, partNum: {}", projectId, fileName, partNum);
                    return sb.toString();
                }
            } else {
                //分片续传，检查远程是否存在文件
                FTPFile ftpFile = ftpClient.mdtmFile(fileName);
                if (Objects.nonNull(ftpFile)) {
                    try (OutputStream os = ftpClient.appendFileStream(fileName)) {
                        IOUtils.copy(is, os);
                        log.info(" ----- upload part file success, projectId: {}, file: {}, partNum: {}", projectId, fileName, partNum);
                        return sb.toString();
                    }
                }
            }
        } catch (Exception e) {
            log.error(" ----- upload part file failed, ", e);
        } finally {
            close(ftpClient);
            try {
                is.close();
            } catch (IOException e) {
                log.error(" ----- inputStream close failed, ", e);
            }
        }
        log.error(" ----- upload part file failed because file not exists, projectId: {}, file: {}, partNum: {}", projectId, fileName, partNum);
        return null;
    }

    /**
     * 删除ftp上的文件
     *
     * @param host     ftp服务器地址
     * @param port     ftp端口
     * @param username ftp用户
     * @param password ftp密码
     * @param filePath 资源文件
     * @return true || false
     */
    public static boolean remove(String host, Integer port, String username, String password, String filePath) {
        FTPClient ftpClient = initClient(host, port, username, password);
        if (Objects.isNull(ftpClient) || StringUtils.isBlank(filePath)) {
            return false;
        }
        try {
            changeDirectory(ftpClient, splitFtpFilePath(filePath));
            if (ftpClient.deleteFile(filePath.substring(filePath.lastIndexOf(SEPARATOR) + 1))) {
                log.info(" ----- remove file success, file: {}", filePath);
                if (ArrayUtils.isEmpty(ftpClient.listFiles())) {
                    removeEmptyDir(ftpClient, ftpClient.printWorkingDirectory());
                }
                return true;
            }
            log.error(" ----- remove file failed, file: {}", filePath);
        } catch (Exception e) {
            log.error(" ----- remove file failed, file: {}", filePath, e);
        } finally {
            close(ftpClient);
        }
        return false;
    }

    /**
     * @param host       ftp服务器地址
     * @param port       ftp端口
     * @param username   ftp用户
     * @param password   ftp密码
     * @param filePath   资源路径
     * @param targetPath 目标路径
     * @return 文件
     */
    public static File download(String host, Integer port, String username, String password, String filePath, String targetPath) {
        FTPClient ftpClient = initClient(host, port, username, password);
        if (Objects.isNull(ftpClient) || StringUtils.isBlank(filePath)) {
            return null;
        }
        if (!FileUtil.checkDirectoryPath(targetPath)) {
            return null;
        }
        // ----- 获取文件名和文件
        String fileName = filePath.substring(filePath.lastIndexOf(SEPARATOR) + 1);
        File file = new File(targetPath + SEPARATOR + fileName);
        try (OutputStream os = new FileOutputStream(file)) {
            // ----- 切换到对应目录
            changeDirectory(ftpClient, splitFtpFilePath(filePath));
            ftpClient.setBufferSize(1024);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            FTPFile ftpFile = ftpClient.mdtmFile(fileName);
            // ----- 判断文件是否存在
            Assert.notNull(ftpFile, "file not exists");
            // ----- 下载
            if (ftpClient.retrieveFile(fileName, os)) {
                log.info(" ----- download file success, filePath: {}, targetPath: {}", filePath, targetPath);
                return file;
            }
            log.error(" ----- download file failed, filePath: {}, targetPath: {}", filePath, targetPath);
        } catch (Exception e) {
            log.error(" ----- download file failed, filePath: {}, targetPath: {}", filePath, targetPath, e);
        } finally {
            close(ftpClient);
        }
        return null;
    }

    /**
     * 判断文件是否存在
     *
     * @param host     ftp服务器地址
     * @param port     ftp端口
     * @param username ftp用户
     * @param password ftp密码
     * @param filePath 文件地址
     * @return true || false
     */
    public static boolean exists(String host, Integer port, String username, String password, String filePath) {
        FTPClient ftpClient = initClient(host, port, username, password);
        if (Objects.isNull(ftpClient) || StringUtils.isBlank(filePath)) {
            return false;
        }
        try {
            String fileName = filePath.substring(filePath.lastIndexOf(SEPARATOR) + 1);
            changeDirectory(ftpClient, splitFtpFilePath(filePath));
            FTPFile ftpFile = ftpClient.mdtmFile(fileName);
            log.info(" ----- file exists success, filePath: {}, exists: {}", filePath, Objects.nonNull(ftpFile));
            return Objects.nonNull(ftpFile);
        } catch (Exception e) {
            log.error(" ----- file exists failed, filePath: {}", filePath, e);
        } finally {
            close(ftpClient);
        }
        return false;
    }

    /**
     * 释放资源
     */
    private static void close(FTPClient ftpClient) {
        if (Objects.isNull(ftpClient)) {
            return;
        }
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                log.error(" ----- close ftp server failed, ", e);
            }
        }
    }

    /**
     * 删除文件时清除空的文件夹
     *
     * @param ftpClient ftp客户端
     * @param emptyDir  文件夹地址
     */
    private static void removeEmptyDir(FTPClient ftpClient, String emptyDir) {
        if (Objects.isNull(ftpClient) || StringUtils.isBlank(emptyDir)) {
            return;
        }
        try {
            ftpClient.changeToParentDirectory();
            if (ftpClient.removeDirectory(emptyDir.substring(emptyDir.lastIndexOf(SEPARATOR) + 1))) {
                log.info(" ----- remove empty dir success, file: {}", emptyDir);
                if (ArrayUtils.isEmpty(ftpClient.listFiles())) {
                    removeEmptyDir(ftpClient, ftpClient.printWorkingDirectory());
                }
                return;
            }
            log.error(" ----- remove empty dir failed, file: {}", emptyDir);
        } catch (Exception e) {
            log.error(" ----- remove empty dir failed, file: {}", emptyDir, e);
        }
    }

    /**
     * 切换到对应的目录，当目录不存在时，创建目录
     *
     * @param filePath 文件目录
     */
    private static void changeDirectory(FTPClient ftpClient, String... filePath) throws Exception {
        for (String path : filePath) {
            if (StringUtils.isBlank(path) || ftpClient.changeWorkingDirectory(path)) {
                continue;
            }
            ftpClient.makeDirectory(path);
            ftpClient.changeWorkingDirectory(path);
        }
    }

    /**
     * 根据FTP文件地址切割出FTP文件目录
     *
     * @param filePath 文件地址
     * @return 文件所在目录
     */
    private static String[] splitFtpFilePath(String filePath) {
        Assert.isTrue(StringUtils.isNotBlank(filePath), "file path is null");
        // ----- 去掉尾[文件名]
        filePath = filePath.substring(0, filePath.lastIndexOf(SEPARATOR));
        return filePath.split(SEPARATOR);
    }

    /**
     * 根据用户ID获取目录
     *
     * @param userId 用户ID
     * @return 目录
     */
    private static String getFilePath(Long userId, String targetPath) {
        StringBuilder sb = new StringBuilder();
        targetPath = targetPath == null ? DEFAULT_PATH : targetPath;
        LocalDate now = LocalDate.now();
        return sb.append(targetPath).append(SEPARATOR)
                .append(userId).append(SEPARATOR)
                .append(now.getYear()).append(SEPARATOR)
                .append(now.getMonthValue()).append(SEPARATOR)
                .append(now.getDayOfMonth()).toString();
    }

}
