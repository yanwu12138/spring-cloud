package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author Baofeng Xu
 * @date 2023-03-02 002 19:25:45.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class TelnetUtil {

    private TelnetUtil() {
        throw new UnsupportedOperationException("TelnetUtil should never be instantiated");
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
     * 写入命令方法
     */
    private static void writeUtil(String cmd, OutputStream os) throws IOException {
        cmd = cmd + "\n";
        os.write(cmd.getBytes());
        os.flush();
    }

    /***
     * 读到指定位置,不在向下读
     */
    private static String readUntil(String endFlag, InputStreamReader reader) throws IOException {
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
