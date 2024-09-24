package com.yanwu.spring.cloud.common.pojo;

import ch.ethz.ssh2.Connection;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2023-03-02 002 19:31:49.
 * <p>
 * description:
 */
@Data
@Slf4j
@Accessors(chain = true)
public class RemoteServer implements Serializable {
    private static final long serialVersionUID = -4237046095866390344L;
    protected static final Integer DEFAULT_PORT = 22;

    /*** 服务器地址 ***/
    public String host;
    /*** 服务器端口 ***/
    public Integer port;
    /*** 服务器账户 ***/
    public String account;
    /*** 服务器校验方式：密码或者publicKey文件 ***/
    public String password;
    /*** 校验类型 ***/
    public VerifyType verifyType;

    protected RemoteServer() {
    }

    public static RemoteServer getInstance(String host, String account, String password) {
        return getInstance(host, DEFAULT_PORT, account, password);
    }

    public static RemoteServer getInstance(String host, Integer port, String account, String password) {
        return getInstance(host, port, account, password, VerifyType.PASSWORD);
    }

    public static RemoteServer getInstance(String host, String account, String password, VerifyType verifyType) {
        return getInstance(host, DEFAULT_PORT, account, password, verifyType);
    }

    public static RemoteServer getInstance(String host, Integer port, String account, String password, VerifyType verifyType) {
        if (StringUtils.isBlank(host) || StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            log.error("remote server getInstance failed. host: {}, account: {}, password: {}", host, account, password);
            throw new RuntimeException("remote server get instance failed.");
        }
        RemoteServer instance = new RemoteServer();
        instance.setHost(host);
        instance.setPort(port);
        instance.setAccount(account);
        instance.setPassword(password);
        instance.setVerifyType(verifyType);
        return instance;
    }

    /**
     * 校验用户身份
     *
     * @param connection 连接
     * @return 【true: 校验通过; false: 校验不通过】
     */
    public boolean connectionVerify(Connection connection) throws Exception {
        switch (verifyType) {
            case PUBLIC_KEY:
                return connection.authenticateWithPublicKey(account, new File(password), null);
            case PASSWORD:
            default:
                return connection.authenticateWithPassword(account, password);
        }
    }

    @Getter
    public enum VerifyType {
        PASSWORD, PUBLIC_KEY,
        ;
    }
}
