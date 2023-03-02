package com.yanwu.spring.cloud.common.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Baofeng Xu
 * @date 2023-03-02 002 19:23:13.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class SshUtil {

    private SshUtil() {
        throw new UnsupportedOperationException("SshUtil should never be instantiated");
    }

    public static void main(String[] args) throws Exception {
        System.out.println(execCommand("192.168.18.254", "root", "tcjjxsj3", "ping 192.168.18.254 -c 4 -w 5"));
        System.out.println(checkDiskOccupy("home"));
        System.out.println(checkDiskOccupy("root"));
    }

    private static String checkDiskOccupy(String partition) {
        String command = "df -h | grep " + partition + " | awk '{print $5}'";
        String diskOccupy = execCommand("192.168.18.254", "root", "tcjjxsj3", command);
        if (StringUtils.isBlank(diskOccupy)) {
            return partition + "分区磁盘占用检查异常";
        }
        if (diskOccupy.compareTo("21%") > 0) {
            return partition + "分区磁盘占用过高，" + diskOccupy;
        } else {
            return partition + "分区磁盘占用正常，" + diskOccupy;
        }
    }

    /***
     * 远程执行shell脚本或者命令，默认使用22端口
     * @param ip       服务器IP
     * @param username 登录服务器的用户名
     * @param password 登录服务器的密码
     * @return 执行结果
     */
    public static String execCommand(String ip, String username, String password, String cmd) {
        return execCommand(ip, 22, username, password, cmd);
    }

    /***
     * 远程执行shell脚本或者命令
     * @param ip       服务器IP
     * @param port     端口
     * @param username 登录服务器的用户名
     * @param password 登录服务器的密码
     * @return 执行结果
     */
    public static String execCommand(String ip, Integer port, String username, String password, String cmd) {
        Connection connection = null;
        try {
            connection = getSshConnection(ip, port, username, password);
            return execCommand(connection, cmd);
        } catch (Exception e) {
            log.error("exec command error.", e);
            return null;
        }
    }

    /***
     * 远程执行shell脚本或者命令
     * @param cmd 即将执行的命令
     * @return 命令执行完后返回的结果值
     */
    private static String execCommand(Connection connection, String cmd) {
        if (connection == null) {
            log.warn("exec command failed, because connection is empty.");
            return null;
        }
        String result = "";
        Session session = null;
        try {
            session = connection.openSession();
            session.execCommand(cmd);
            result = processStdout(session.getStdout());
            if (StringUtils.isBlank(result)) {
                log.info("get standard output empty, connection: {}, cmd: {}", connection.getHostname(), cmd);
                result = processStdout(session.getStderr());
            } else {
                log.info("exec command success, connection: {}, cmd: {}", connection.getHostname(), cmd);
            }
        } catch (IOException e) {
            log.error("exec command failed, connection: {}, cmd: {}", connection.getHostname(), cmd, e);
        } finally {
            if (session != null) {
                session.close();
            }
            connection.close();
        }
        return result;
    }


    /***
     * 建立与服务器的连接，默认使用22端口
     * @param ip       服务器IP
     * @param username 登录服务器的用户名
     * @param password 登录服务器的密码
     * @return 返回登录的连接， 在使用的最后一定记得关闭connect资源
     */
    private static Connection getSshConnection(String ip, String username, String password) {
        return getSshConnection(ip, 22, username, password);
    }

    /***
     * 建立与服务器的连接
     * @param ip       服务器IP
     * @param port     端口
     * @param username 登录服务器的用户名
     * @param password 登录服务器的密码
     * @return 返回登录的连接， 在使用的最后一定记得关闭connect资源
     */
    private static Connection getSshConnection(String ip, Integer port, String username, String password) {
        if (!IpMacUtil.checkIpv4(ip)) {
            log.error("get SSH connection failed, because ip format is incorrect.");
            return null;
        }
        Connection connection = null;
        try {
            connection = new Connection(ip, port);
            connection.connect();
            if (connection.authenticateWithPassword(username, password)) {
                return connection;
            } else {
                connection.close();
            }
        } catch (IOException e) {
            log.info("get SSH connection failed.", e);
            connection.close();
        }
        return null;
    }

    /***
     * 解析脚本执行返回的结果集
     * @param in 输入流对象
     * @return 以纯文本的格式返回
     */
    private static String processStdout(InputStream in) {
        StringBuilder buffer = new StringBuilder();
        try (InputStream stdout = new StreamGobbler(in);
             InputStreamReader reader = new InputStreamReader(stdout, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line).append("\n");
            }
        } catch (Exception e) {
            log.info("parsing script error.", e);
        }
        return buffer.toString();
    }

}
