package com.yanwu.spring.cloud.common.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yanwu.spring.cloud.common.pojo.RemoteServer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClientCreator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

/**
 * @author Baofeng Xu
 * @date 2023-02-24 024 11:10:04.
 * <p>
 * description: 使用java执行SCP命令
 */
@Slf4j
public class ScpUtil {

    public static final Long VERIFY_TIMEOUT = 30 * 1000L;

    private ScpUtil() {
        throw new UnsupportedOperationException("ScpUtil should never be instantiated");
    }

    public static void main(String[] args) {
        ScpServer server = ScpServer.getInstance("192.168.56.50", "root", "Js_2643.");
        System.out.println(download(server, "/root/dist.zip", "E:\\download\\test.zip"));
        System.out.println(upload(server, "E:\\download\\test.zip", "/root/test.zip"));
        System.out.println(upload(server, "E:\\download\\music", "/root/music"));
        System.out.println(download(server, "/root/music", "E:\\download\\music111111111"));
    }

    /**
     * SCP下载文件（服务器校验方式默认使用密码校验）
     *
     * @param server 服务器相关配置
     * @param remote 远程文件地址
     * @param local  本地文件地址
     */
    public static boolean download(ScpServer server, String remote, String local) {
        FileUtil.deleteFile(local);
        try {
            if (createSession(server)) {
                server.getScpClient().download(remote, local, ScpClient.Option.Recursive);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("scp download file failed. server: {}, local: {}", server, local);
            return false;
        } finally {
            server.close();
        }
    }

    /**
     * SCP上传文件（服务器校验方式默认使用密码校验）
     *
     * @param server 服务器相关配置
     * @param local  本地文件地址
     * @param remote 远程文件地址
     */
    public static boolean upload(ScpServer server, String local, String remote) {
        if (StringUtils.isBlank(local)) {
            return false;
        }
        File file = new File(local);
        if (!file.exists()) {
            return false;
        }
        try {
            if (createSession(server)) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files == null || files.length == 0) {
                        return false;
                    }
                    for (File item : files) {
                        server.getScpClient().upload(item.toPath(), remote, ScpClient.Option.Recursive);
                    }
                } else {
                    server.getScpClient().upload(local, remote, ScpClient.Option.Recursive);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("scp upload file failed. server: {}, local: {}", server, local, e);
            return false;
        } finally {
            server.close();
        }
    }

    /**
     * 创建会话
     */
    private static boolean createSession(ScpServer server) {
        try {
            // ----- 创建SSH客户端
            SshClient client = SshClient.setUpDefaultClient();
            client.start();
            ClientSession session = client.connect(server.getAccount(), server.getHost(), server.getPort()).verify(VERIFY_TIMEOUT).getSession();
            // ----- 密码认证
            server.sessionVerify(session);
            if (session.auth().verify(VERIFY_TIMEOUT).isSuccess()) {
                // ----- 认证成功返回相关客户端信息
                ScpClientCreator creator = ScpClientCreator.instance();
                server.setSshClient(client);
                server.setSession(session);
                server.setScpClient(creator.createScpClient(session));
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("scp create client failed.", e);
            server.close();
            return false;
        }
    }

    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class ScpServer extends RemoteServer implements AutoCloseable {
        private static final long serialVersionUID = -5638428580056712820L;

        /*** SCP相关客户端 ***/
        @JsonIgnore
        public SshClient sshClient;
        @JsonIgnore
        public ScpClient scpClient;
        @JsonIgnore
        public ClientSession session;

        protected ScpServer() {
        }

        public static ScpServer getInstance(String host, String account, String password) {
            return getInstance(host, DEFAULT_PORT, account, password);
        }

        public static ScpServer getInstance(String host, Integer port, String account, String password) {
            return getInstance(host, port, account, password, VerifyType.PASSWORD);
        }

        public static ScpServer getInstance(String host, String account, String password, VerifyType verifyType) {
            return getInstance(host, DEFAULT_PORT, account, password, verifyType);
        }

        public static ScpServer getInstance(String host, Integer port, String account, String password, VerifyType verifyType) {
            if (StringUtils.isBlank(host) || StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
                log.error("remote server getInstance failed. host: {}, account: {}, password: {}", host, account, password);
                throw new RuntimeException("remote server get instance failed.");
            }
            ScpServer instance = new ScpServer();
            instance.setHost(host);
            instance.setPort(port);
            instance.setAccount(account);
            instance.setPassword(password);
            instance.setVerifyType(verifyType);
            return instance;
        }

        /*** 释放资源 ***/
        @Override
        public void close() {
            scpClient = null;
            try {
                if (session != null) {
                    session.close();
                }
            } catch (Exception ignored) {
            } finally {
                session = null;
            }
            try {
                if (sshClient != null) {
                    sshClient.stop();
                    sshClient.close();
                }
            } catch (Exception ignored) {
            } finally {
                sshClient = null;
            }
        }

        /*** 根据不通的校验类型来执行用户验证 ***/
        public void sessionVerify(ClientSession session) throws Exception {
            switch (verifyType) {
                case PUBLIC_KEY:
                    session.addPublicKeyIdentity(getKeyPair());
                    break;
                case PASSWORD:
                default:
                    session.addPasswordIdentity(password);
            }
        }

        private KeyPair getKeyPair() throws Exception {
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream()) {
                byteArrayStream.write(FileUtil.read(password));
                try (ObjectOutputStream objectStream = new ObjectOutputStream(byteArrayStream)) {
                    objectStream.writeObject(keyPair);
                }
            }
            return keyPair;
        }

        @Override
        public String toString() {
            return JsonUtil.toString(this);
        }
    }

}
