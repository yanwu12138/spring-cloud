package com.yanwu.spring.cloud.netty.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yanwu.spring.cloud.netty.util.NettyUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.NetworkInterface;
import java.util.Objects;

/**
 * @author Baofeng Xu
 * @date 2021/4/25 10:24.
 * <p>
 * description:
 */
@Configuration
public class NettyConfig {

    /*** tcp端口 ***/
    @Getter
    @Value("${netty.interface.name}")
    private String interfaceName;

    @Getter
    @Value("${netty.source.ip}")
    private String sourceIp;

    /*** tcp端口 ***/
    @Getter
    @Value("${netty.tcp.port}")
    private Integer tcpPort;

    /*** udp端口 ***/
    @Getter
    @Value("${netty.udp.port}")
    private Integer udpPort;

    /*** 广播端口 ***/
    @Getter
    @Value("${netty.radio.port}")
    private Integer radioPort;

    /*** 组播文件地址 ***/
    @Getter
    @Value("${netty.broadcast.ip}")
    private String broadcastIp;
    /*** 组播文件端口 ***/
    @Getter
    @Value("${netty.broadcast.port}")
    private Integer broadcastPort;

    @JsonIgnore
    public NetworkInterface getInter() {
        NetworkInterface anInterface = NettyUtils.getInterface(interfaceName);
        if (Objects.isNull(anInterface)) {
            throw new RuntimeException("interface" + interfaceName + " 不存在");
        }
        return anInterface;
    }
}
