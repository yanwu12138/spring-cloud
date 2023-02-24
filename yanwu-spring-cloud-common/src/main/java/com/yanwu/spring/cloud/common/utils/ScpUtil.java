package com.yanwu.spring.cloud.common.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.keyverifier.DefaultKnownHostsServerKeyVerifier;
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
        Server server = Server.getInstance("192.168.56.50", "root", "Js_2643.");
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
    public static boolean download(Server server, String remote, String local) {
        return download(server, remote, local, VerifyEnum.PASSWORD);
    }

    /**
     * SCP下载文件
     *
     * @param server     服务器相关配置
     * @param remote     远程文件地址
     * @param local      本地文件地址
     * @param verifyType 服务器校验方式
     */
    public static boolean download(Server server, String remote, String local, VerifyEnum verifyType) {
        FileUtil.deleteFile(local);
        try {
            server.setVerifyType(verifyType);
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
    public static boolean upload(Server server, String local, String remote) {
        return upload(server, local, remote, VerifyEnum.PASSWORD);
    }


    /**
     * SCP上传文件
     *
     * @param server     服务器相关配置
     * @param local      本地文件地址
     * @param remote     远程文件地址
     * @param verifyType 服务器校验方式
     */
    public static boolean upload(Server server, String local, String remote, VerifyEnum verifyType) {
        if (StringUtils.isBlank(local)) {
            return false;
        }
        File file = new File(local);
        if (!file.exists()) {
            return false;
        }
        try {
            server.setVerifyType(verifyType);
            if (createSession(server)) {
                server.getScpClient().upload(local, remote, ScpClient.Option.Recursive);
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
    private static boolean createSession(Server server) {
        if (server == null || !server.check()) {
            return false;
        }
        try {
            // ----- 创建SSH客户端
            SshClient client = SshClient.setUpDefaultClient();
            client.setServerKeyVerifier(new DefaultKnownHostsServerKeyVerifier((clientSession, remoteAddress, serverKey) -> false, true));
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
    public static class Server implements AutoCloseable {
        /*** 服务器地址 ***/
        public String host;
        /*** 服务器端口 ***/
        public Integer port;
        /*** 服务器账户 ***/
        public String account;
        /*** 校验类型 ***/
        public VerifyEnum verifyType;
        /*** 服务器校验方式：密码或者publicKey文件 ***/
        public String verify;

        /*** SCP相关客户端 ***/
        @JsonIgnore
        public SshClient sshClient;
        @JsonIgnore
        public ScpClient scpClient;
        @JsonIgnore
        public ClientSession session;


        @SuppressWarnings("all")
        private static Server getInstance(String host, String account, String verify) {
            return getInstance(host, 22, account, verify);
        }

        @SuppressWarnings("all")
        private static Server getInstance(String host, Integer port, String account, String verify) {
            return new Server().setHost(host).setPort(port).setAccount(account).setVerify(verify);
        }

        /*** 检查参数是否合法 ***/
        public boolean check() {
            if (!IpMacUtil.checkIpv4(host)) {
                return false;
            }
            if (StringUtils.isBlank(account)) {
                return false;
            }
            return !StringUtils.isBlank(verify);
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
                    session.addPasswordIdentity(verify);
            }
        }

        private KeyPair getKeyPair() throws Exception {
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
            try (ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream()) {
                byteArrayStream.write(FileUtil.read(verify));
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

    @Getter
    public enum VerifyEnum {
        PASSWORD, PUBLIC_KEY,
        ;
    }

}
