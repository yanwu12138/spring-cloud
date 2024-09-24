package com.yanwu.spring.cloud.common.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import com.yanwu.spring.cloud.common.pojo.RemoteServer;
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

    private static String checkDiskOccupy(String partition) {
        String command = "df -h | grep " + partition + " | awk '{print $5}'";
        RemoteServer server = RemoteServer.getInstance("192.168.56.50", "root", "Js_2643.");
        String diskOccupy = execCommand(server, command);
        if (StringUtils.isBlank(diskOccupy)) {
            return partition + "分区磁盘占用检查异常";
        }
        if (diskOccupy.compareTo("21%") > 0) {
            return partition + "分区磁盘占用过高，" + diskOccupy;
        } else {
            return partition + "分区磁盘占用正常，" + diskOccupy;
        }
    }

    /**
     * 检查远程服务器密码是否正确
     *
     * @param server 服务器相关配置
     * @return 【true: 正确; false: 不正确】
     */
    public static boolean checkRemote(RemoteServer server) {
        Connection connection = null;
        try {
            connection = new Connection(server.getHost(), server.getPort());
            connection.connect();
            return server.connectionVerify(connection);
        } catch (Exception e) {
            log.info("check server password failed, server: {}", server, e);
            return false;
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * 远程执行shell脚本或者命令
     *
     * @param server 服务器相关配置
     * @param cmd    即将执行的命令
     * @return 命令执行完后返回的结果值
     */
    public static String execCommand(RemoteServer server, String cmd) {
        Connection connection = getSshConnection(server);
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

    /**
     * 建立与服务器的连接
     *
     * @param server 服务器相关信息
     * @return 返回登录的连接， 在使用的最后一定记得关闭connect资源
     */
    private static Connection getSshConnection(RemoteServer server) {
        Connection connection = null;
        try {
            connection = new Connection(server.getHost(), server.getPort());
            connection.connect();
            if (server.connectionVerify(connection)) {
                return connection;
            } else {
                connection.close();
            }
        } catch (Exception e) {
            log.info("get SSH connection failed.", e);
            if (connection != null) {
                connection.close();
            }
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
        try (InputStream stdout = new StreamGobbler(in); InputStreamReader reader = new InputStreamReader(stdout, StandardCharsets.UTF_8); BufferedReader br = new BufferedReader(reader)) {
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
