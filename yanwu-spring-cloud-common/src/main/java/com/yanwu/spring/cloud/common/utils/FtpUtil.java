package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

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
public class FtpUtil {

    private static final String DEFAULT_PATH = "test";
    private static final String SEPARATOR = "/";

    private static final String FTP_HOST = "192.168.1.158";
    private static final Integer FTP_PORT = 21;
    private static final String USERNAME = "hoolink";
    private static final String PASSWORD = "hoolink123";

    private static FTPClient ftpClient;
    private static FtpUtil instance;

    private FtpUtil() {
    }

    static {
        instance = new FtpUtil();
        ftpClient = new FTPClient();
    }

    /**
     * 获取工具类示例
     *
     * @return FtpUtil
     */
    public static FtpUtil getInstance() {
        return instance;
    }

    /**
     * 初始化FTP
     */
    private void initClient(String host, Integer port, String username, String password) {
        if (StringUtils.isBlank(host)) {
            throw new RuntimeException("init ftp client failed, host is null");
        }
        // ----- 当端口为空时使用默认端口
        port = port == null ? FTP_PORT : port;
        try {
            ftpClient.setControlEncoding("UTF-8");
            // ----- 连接
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            // ----- 检测连接是否成功
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                log.info(" ----- connect success ftp server host: {}, post: {}, user: {}", host, port, username);
                return;
            }
            log.error(" ----- connect failed ftp server host: {}, post: {}, user: {}", host, port, username);
        } catch (Exception e) {
            log.error(" ----- connect failed ftp server, ", e);
        }
        instance.close();
    }

    /**
     * ftp上传文件
     *
     * @param file      文件
     * @param projectId 项目ID
     * @return true||false
     */
    public String upload(File file, Long projectId, String filePath) {
        return instance.upload(FTP_HOST, FTP_PORT, USERNAME, PASSWORD, file, projectId, filePath);
    }

    /**
     * 上传文件
     *
     * @param host       ftp服务器地址
     * @param port       ftp端口
     * @param username   ftp用户
     * @param password   ftp密码
     * @param file       文件
     * @param projectId  项目ID
     * @param targetPath 文件存放目录
     * @return true||false
     */
    public String upload(String host, Integer port, String username, String password,
                         File file, Long projectId, String targetPath) {
        instance.initClient(host, port, username, password);
        if (Objects.isNull(ftpClient) || Objects.isNull(file)) {
            return null;
        }
        String filePath = getFilePath(projectId, targetPath);
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file)) {
            // ----- 切换到对应目录
            changeDirectory(filePath.split(SEPARATOR));
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("GBK");
            // ----- 设置文件类型
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // ----- 上传
            if (!ftpClient.storeFile(file.getName(), fis)) {
                log.error(" ----- upload file failed, projectId: {}, file: {}", projectId, file.getName());
                return null;
            } else {
                sb.append(filePath).append(SEPARATOR).append(file.getName());
                log.info(" ----- upload file success, file: {}", sb.toString());
            }
        } catch (Exception e) {
            log.error(" ----- upload file failed, ", e);
        } finally {
            instance.close();
        }
        return sb.toString();
    }

    /**
     * 删除ftp上的文件
     *
     * @param target 资源文件
     * @return true || false
     */
    public boolean remove(String target) {
        return instance.remove(FTP_HOST, FTP_PORT, USERNAME, PASSWORD, target);
    }

    /**
     * 删除ftp上的文件
     *
     * @param host     ftp服务器地址
     * @param port     ftp端口
     * @param username ftp用户
     * @param password ftp密码
     * @param target   资源文件
     * @return true || false
     */
    public boolean remove(String host, Integer port, String username, String password, String target) {
        instance.initClient(host, port, username, password);
        if (Objects.isNull(ftpClient) || StringUtils.isBlank(target)) {
            return false;
        }
        boolean flag = false;
        try {
            changeDirectory(splitFtpFilePath(target));
            flag = ftpClient.deleteFile(target.substring(target.lastIndexOf(SEPARATOR) + 1));
            log.info(" ----- remove file {}, file: {}", flag ? "success" : "failed", target);
        } catch (Exception e) {
            log.error(" ----- remove file failed, ", e);
        } finally {
            instance.close();
        }
        return flag;
    }

    /**
     * @param filePath   资源路径
     * @param targetPath 目标路径
     * @return 文件
     */
    public File download(String filePath, String targetPath) {
        return download(FTP_HOST, FTP_PORT, USERNAME, PASSWORD, filePath, targetPath);
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
    public File download(String host, Integer port, String username, String password, String filePath, String targetPath) {
        instance.initClient(host, port, username, password);
        if (Objects.isNull(ftpClient) || StringUtils.isBlank(filePath)) {
            return null;
        }
        File file = null;
        try {
            // ----- 切换到对应目录
            changeDirectory(splitFtpFilePath(filePath));
            // ----- 获取文件名和文件
            String fileName = filePath.substring(filePath.lastIndexOf(SEPARATOR) + 1);
            FTPFile ftpFile = ftpClient.mdtmFile(fileName);
            if (Objects.nonNull(ftpFile)) {
                file = new File(targetPath + SEPARATOR + fileName);
                // ----- 下载
                try (OutputStream out = new FileOutputStream(file)) {
                    ftpClient.retrieveFile(fileName, out);
                }
            }
            log.info(" ----- download file {}, filePath: {}, targetPath: {}", file != null ? "success" : "failed", filePath, targetPath);
        } catch (Exception e) {
            log.error(" ----- download file failed, ", e);
        } finally {
            instance.close();
        }
        return file;
    }

    /**
     * 释放资源
     *
     * @author tangw 2010-12-26
     */
    public void close() {
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
     * 切换到对应的目录，当目录不存在时，创建目录
     *
     * @param filePath 文件目录
     */
    private void changeDirectory(String[] filePath) throws Exception {
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
    private String[] splitFtpFilePath(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            throw new NullPointerException("file path is null");
        }
        // ----- 去掉尾[文件名]
        filePath = filePath.substring(0, filePath.lastIndexOf(SEPARATOR));
        return filePath.split(SEPARATOR);
    }

    /**
     * 根据项目ID获取目录
     *
     * @param projectId 项目ID
     * @return 目录
     */
    private static String getFilePath(Long projectId, String targetPath) {
        StringBuilder sb = new StringBuilder();
        targetPath = targetPath == null ? DEFAULT_PATH : targetPath;
        LocalDate now = LocalDate.now();
        return sb.append(targetPath).append(SEPARATOR)
                .append(projectId).append(SEPARATOR)
                .append(now.getYear()).append(SEPARATOR)
                .append(now.getMonthValue()).append(SEPARATOR)
                .append(now.getDayOfMonth()).toString();
    }

    public static void main(String[] args) {
        FtpUtil instance = FtpUtil.getInstance();
        String filePath = "F:\\document\\协议文档\\02 HOOLINK协议\\文档\\HOOLINK传输协议.docx";
        String targetPath = "F:\\file";
        Long projectId = 269L;
        String file = instance.upload(new File(filePath), projectId, DEFAULT_PATH);
        instance.download(file, targetPath);
        instance.remove(file);
    }

}