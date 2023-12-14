package com.yanwu.spring.cloud.netty.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yanwu.spring.cloud.netty.util.NettyUtil;
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

    /*** 网卡名称 ***/
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

    /*** 串口号 ***/
    @Getter
    @Value("${serialport.serialNumber:COM3}")
    private String serialNumber;

    /*** 波特率 ***/
    @Getter
    @Value("${serialport.baudRate:11255}")
    private Integer baudRate;

    /*** 校验位 ***/
    @Getter
    @Value("${serialport.checkoutBit:0}")
    private Integer checkoutBit;

    /*** 数据位 ***/
    @Getter
    @Value("${serialport.dataBit:8}")
    private Integer dataBit;

    /*** 停止位 ***/
    @Getter
    @Value("${serialport.stopBit:1}")
    private Integer stopBit;

    @Getter
    private int serverWorker;

    @JsonIgnore
    public NetworkInterface getInter() {
        NetworkInterface anInterface = NettyUtil.getInterface(interfaceName);
        if (Objects.isNull(anInterface)) {
            throw new RuntimeException("interface: " + interfaceName + " 不存在");
        }
        return anInterface;
    }
}
