package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Baofeng Xu
 * @date 2022/8/9 17:08.
 * <p>
 * description: 使用wget下载文件(默认断点续传)
 * <p>
 */
@Slf4j
@SuppressWarnings("unused")
public class WgetUtil {
    private static final String SPLIT = " ";

    private WgetUtil() {
        throw new UnsupportedOperationException("WgetUtil should never be instantiated");
    }

    /***
     * 通过wget下载HTTP资源（默认重试5次 && 超时时间为60S）
     * @param url  资源地址
     * @param path 输出路径
     */
    public static boolean wgetHttp(String url, String path) {
        return wgetHttp(url, path, null, null);
    }

    /***
     * 通过wget下载HTTP资源（默认重试5次 && 超时时间为60S）
     * @param url      资源地址
     * @param path     输出路径
     * @param username 用户名
     * @param password 密码
     */
    public static boolean wgetHttp(String url, String path, String username, String password) {
        return wgetHttp(url, path, 5, username, password);
    }

    /***
     * 通过wget下载HTTP资源（默认超时时间为60S）
     * @param url    资源地址
     * @param path   输出路径
     * @param tryNnm 重试次数
     */
    public static boolean wgetHttp(String url, String path, int tryNnm) {
        return wgetHttp(url, path, tryNnm, null, null);
    }

    /***
     * 通过wget下载HTTP资源（默认超时时间为60S）
     * @param url      资源地址
     * @param path     输出路径
     * @param tryNnm   重试次数
     * @param username 用户名
     * @param password 密码
     */
    public static boolean wgetHttp(String url, String path, int tryNnm, String username, String password) {
        return wgetHttp(url, path, tryNnm, 60, username, password);
    }

    /***
     * 通过wget下载HTTP资源（默认超时时间为60S）
     * @param url     资源地址
     * @param path    输出文件
     * @param tryNnm  重试次数
     * @param timeout 超时时间
     */
    public static boolean wgetHttp(String url, String path, int tryNnm, int timeout) {
        return wgetHttp(url, path, tryNnm, timeout, null, null);
    }

    /***
     * 通过wget下载HTTP资源（默认超时时间为60S）
     * @param url      资源地址
     * @param path     输出文件
     * @param tryNnm   重试次数
     * @param timeout  超时时间
     * @param username 用户名
     * @param password 密码
     */
    public static boolean wgetHttp(String url, String path, int tryNnm, int timeout, String username, String password) {
        if (StringUtils.isBlank(url)) {
            log.error("wget http failed, because url is empty.");
            return false;
        }
        if (StringUtils.isBlank(path)) {
            log.error("wget http failed, because path is empty.");
            return false;
        }
        if (tryNnm <= 0) {
            log.error("wget http failed, because tryNum cannot be less than 0.");
            return false;
        }
        if (timeout <= 0) {
            log.error("wget http failed, because timeout cannot be less than 0.");
            return false;
        }
        return execWget(path, assemblyCmd(url, path, tryNnm, timeout, username, password, Boolean.FALSE), tryNnm);
    }

    /***
     * 通过wget下载HTTP资源（默认重试5次 && 超时时间为60S）
     * @param url      资源地址
     * @param path     输出路径
     * @param username 用户名
     * @param password 密码
     */
    public static boolean wgetFtp(String url, String path, String username, String password) {
        return wgetFtp(url, path, username, password, 5);
    }

    /***
     * 通过wget下载HTTP资源（默认重试5次 && 超时时间为60S）
     * @param url      资源地址
     * @param path     输出路径
     * @param username 用户名
     * @param password 密码
     * @param tryNnm   重试次数
     */
    public static boolean wgetFtp(String url, String path, String username, String password, int tryNnm) {
        return wgetFtp(url, path, username, password, tryNnm, 60);
    }

    /***
     * 通过wget下载HTTP资源（默认重试5次 && 超时时间为60S）
     * @param url      资源地址
     * @param path     输出路径
     * @param username 用户名
     * @param password 密码
     * @param tryNnm   重试次数
     * @param timeout  超时时间
     */
    public static boolean wgetFtp(String url, String path, String username, String password, int tryNnm, int timeout) {
        if (StringUtils.isBlank(url)) {
            log.error("wget ftp failed, because url is empty.");
            return false;
        }
        if (StringUtils.isBlank(path)) {
            log.error("wget ftp failed, because path is empty.");
            return false;
        }
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            log.error("wget ftp failed, because username || password is empty.");
            return false;
        }
        if (tryNnm <= 0) {
            log.error("wget ftp failed, because tryNum cannot be less than 0.");
            return false;
        }
        if (timeout <= 0) {
            log.error("wget ftp failed, because timeout cannot be less than 0.");
            return false;
        }
        return execWget(path, assemblyCmd(url, path, tryNnm, timeout, username, password, Boolean.TRUE), tryNnm);
    }

    /***
     * 组装wget下载语句
     * @param url      资源地址
     * @param path     输出路径
     * @param tryNnm   重试次数
     * @param timeout  超时时间
     * @param username 资源用户名
     * @param password 资源密码
     * @param isFtp    是否是FTP资源
     */
    private static String assemblyCmd(String url, String path, int tryNnm, int timeout, String username, String password, boolean isFtp) {
        StringBuilder command = new StringBuilder();
        SystemUtil.SystemType systemType = SystemUtil.getSystemType();
        if (SystemUtil.isWindows()) {
            command.append("wget.exe -c ");
        } else {
            command.append("wget -c ");
        }
        command.append(url).append(" -O ").append(path);
        if (tryNnm > 0) {
            command.append(" -t ").append(tryNnm);
        }
        if (timeout > 0) {
            command.append(" -T ").append(timeout).append(" -w ").append(timeout);
        }
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            if (isFtp) {
                command.append(" --ftp-user=").append(username).append(" --ftp-password=").append(password);
            } else {
                command.append(" --http-user=").append(username).append(" --http-password=").append(password);
            }
        }
        if (isFtp) {
            command.append(" --no-passive-ftp");
        }
        return command.toString();
    }

    /***
     * 执行wget下载语句
     * @param path    输出路径
     * @param command wget语句
     * @param tryNnm  重试次数
     */
    private static boolean execWget(String path, String command, int tryNnm) {
        if (StringUtils.isBlank(command)) {
            log.error("wget failed, because command is empty.");
            return false;
        }
        try {
            FileUtil.deleteFile(path);
            FileUtil.checkFilePath(path, true);
        } catch (Exception e) {
            log.error("wget failed, because file processing failed. path: {}, cmd: {}.", path, command, e);
            return false;
        }
        int num = 0;
        do {
            if (execWget(command)) {
                return true;
            }
            num++;
        } while (num < tryNnm);
        return false;
    }

    /***
     * 根据操作系统类型执行执行不同的函数
     * @param command wget语句
     */
    private static boolean execWget(String command) {
        if (StringUtils.isBlank(command)) {
            return false;
        }
        SystemUtil.SystemType systemType = SystemUtil.getSystemType();
        switch (systemType) {
            case WINDOWS:
                return execWinWget(command);
            case LINUX:
            case MAC_OS:
                return execLinuxWget(command);
            default:
                return false;
        }
    }

    /***
     * Windows-wget
     * @param command wget语句
     */
    private static boolean execWinWget(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder().command("cmd", "/c", command).inheritIO();
            if (builder.start().waitFor() == 0) {
                log.info("windows wget success, command: {}", command);
                return true;
            }
        } catch (Exception e) {
            log.error("windows wget failed, cmd: {}.", command, e);
        }
        return false;
    }

    /***
     * Linux-wget
     * @param command wget语句
     */
    private static boolean execLinuxWget(String command) {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
            if (proc.waitFor() == 0) {
                log.info("linux wget success, command: {}", command);
                return true;
            }
        } catch (Exception e) {
            log.error("linux wget failed, cmd: {}.", command, e);
        }
        return false;
    }

}
