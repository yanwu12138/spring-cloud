package com.yanwu.spring.cloud.netty.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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

}
