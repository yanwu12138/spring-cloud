package com.yanwu.spring.cloud.common.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * @author Baofeng Xu
 * @date 2021/8/16 11:31.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class CommandUtil {

    private CommandUtil() {
        throw new UnsupportedOperationException("CommandUtil should never be instantiated");
    }

    /***
     * 通过反射执行对应的函数
     * @param clazz      源对象(被代理对象)
     * @param methodName 被代理方法
     * @param args       被代理方法参数集
     * @return 函数执行结果
     * @throws Exception Exception.class
     */
    public static Object invoke(Class<?> clazz, String methodName, Object... args) throws Exception {
        Method method = clazz.getDeclaredMethod(methodName, getArgType(args));
        Object result = method.invoke(ContextUtil.getBean(clazz), args);
        log.info("invoke. class: {}, method: {}, params: [{}], result: [{}]", clazz.getName(), method.getName(), args, result);
        return result;
    }

    /***
     * 建立与服务器的连接，默认使用22端口
     * @param ip       服务器IP
     * @param username 登录服务器的用户名
     * @param password 登录服务器的密码
     * @return 返回登录的连接， 在使用的最后一定记得关闭connect资源
     */
    public static Connection getSshConnection(String ip, String username, String password) {
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
    public static Connection getSshConnection(String ip, Integer port, String username, String password) {
        if (!IpMacUtil.checkIpv4(ip)) {
            log.error("get SSH connection failed, because ip format is incorrect.");
            return null;
        }
        boolean flag;
        Connection connection = null;
        try {
            connection = new Connection(ip, port);
            connection.connect();
            flag = connection.authenticateWithPassword(username, password);
            if (flag) {
                return connection;
            }
        } catch (IOException e) {
            log.info("get SSH connection failed.", e);
            connection.close();
        }
        return connection;
    }

    public static void main(String[] args) {
        String cmd = "beamselector switch " + 289 + " -f";
        System.out.println(execTelnet("172.28.85.169", "admin", "P@55w0rd!", cmd));
    }

    /***
     * 使用telnet登录然后执行相应的命令
     * @param ip 服务器IP
     * @param username 用户名
     * @param password 密码
     * @param cmd 命令
     * @return 执行结果
     */
    public static Boolean execTelnet(String ip, String username, String password, String cmd) {
        if (!IpMacUtil.checkIpv4(ip)) {
            log.error("telnet client exec command failed, because ip format is incorrect.");
            return Boolean.FALSE;
        }
        TelnetClient client = null;
        try {
            client = new TelnetClient();
            client.connect(ip);
            return execTelnet(client, username, password, cmd);
        } catch (Exception e) {
            log.error("exec telnet error.", e);
            return Boolean.FALSE;
        } finally {
            if (client != null) {
                try {
                    client.disconnect();
                } catch (Exception e) {
                    if (!(e instanceof NullPointerException)) {
                        log.error("client disconnect error.", e);
                    } else {
                        log.error("client disconnect error.");
                    }
                }
            }
        }
    }

    /***
     * 使用telnet登录然后执行相应的命令
     * @param client telnet客户端
     * @param username 用户名
     * @param password 密码
     * @param cmd 命令
     * @return 执行结果
     */
    public static Boolean execTelnet(TelnetClient client, String username, String password, String cmd) {
        if (StringUtils.isBlank(username)) {
            log.error("telnet client exec command failed, because username is empty.");
            return Boolean.FALSE;
        }
        if (StringUtils.isBlank(password)) {
            log.error("telnet client exec command failed, because password is empty.");
            return Boolean.FALSE;
        }
        if (StringUtils.isBlank(cmd)) {
            log.error("telnet client exec command failed, because cmd is empty.");
            return Boolean.FALSE;
        }
        try (InputStream inputStream = client.getInputStream();
             OutputStream outputStream = client.getOutputStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            log.info("telnet client: {}", readUntil(":", reader));
            // ----- 输入用户名
            writeUtil(username, outputStream);
            log.info("telnet client: {}", readUntil(":", reader));
            // ----- 输入密码
            writeUtil(password, outputStream);
            log.info("telnet client: {}", readUntil(".", reader));
            // ----- 执行命令
            writeUtil(cmd, outputStream);
            log.info("telnet client: {}", readUntil(":", reader));
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("exec telnet error.", e);
            return Boolean.FALSE;
        }
    }

    /***
     * 执行SHELL命令
     * @param cmd 命令脚本
     * @return 执行结果
     */
    public static String execCommand(String cmd) throws Exception {
        Process proc = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        IOUtil.close(reader);
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        while ((line = errorReader.readLine()) != null) {
            builder.append(line);
        }
        IOUtil.close(errorReader);
        proc.waitFor();
        log.info("exec shell command, cmd: {}, result: {}", cmd, builder);
        return builder.toString();
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
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /***
     * 远程执行shell脚本或者命令
     * @param cmd 即将执行的命令
     * @return 命令执行完后返回的结果值
     */
    public static String execCommand(Connection connection, String cmd) {
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
        }
        return result;
    }

    /***
     * 获取参数集对应的类型集
     */
    private static Class<?>[] getArgType(Object... args) {
        if (ArrayUtil.isEmpty(args)) {
            return new Class[]{};
        }
        Class<?>[] result = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = args[i].getClass();
        }
        return result;
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

    /***
     * 写入命令方法
     */
    public static void writeUtil(String cmd, OutputStream os) throws IOException {
        cmd = cmd + "\n";
        os.write(cmd.getBytes());
        os.flush();
    }

    /***
     * 读到指定位置,不在向下读
     */
    public static String readUntil(String endFlag, InputStreamReader reader) throws IOException {
        char[] chars = new char[1024];
        boolean flag = false;
        StringBuilder builder = new StringBuilder();
        while (reader.read(chars) != -1) {
            for (char c : chars) {
                builder.append(c);
                // ----- 当拼接的字符串以指定的字符串结尾时,不在继续读
                if (builder.toString().endsWith(endFlag)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                break;
            }
        }
        return builder.toString();
    }
}
