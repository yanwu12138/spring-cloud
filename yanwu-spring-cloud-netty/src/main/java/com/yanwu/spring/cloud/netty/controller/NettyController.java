package com.yanwu.spring.cloud.netty.controller;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.CallableResult;
import com.yanwu.spring.cloud.common.pojo.CommandBO;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.netty.handler.TcpHandler;
import com.yanwu.spring.cloud.netty.handler.UdpHandler;
import com.yanwu.spring.cloud.netty.handler.UpgradeHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 14:45.
 * <p>
 * description:
 */
@Slf4j
@RestController
@RequestMapping("webapp/netty/")
public class NettyController {

    @Resource
    private TcpHandler tcpHandler;
    @Resource
    private UdpHandler udpHandler;
    @Resource
    private UpgradeHandler upgradeHandler;

    @LogParam
    @PostMapping("/tcp/send")
    public void tcpSend(@RequestBody CommandBO<String> command) {
        tcpHandler.send(command.getCtxId(), String.valueOf(command.getData()));
    }

    @LogParam
    @PostMapping("/udp/send")
    public void udpSend(@RequestBody CommandBO<String> command) {
        udpHandler.send(command.getCtxId(), String.valueOf(command.getData()));
    }

    @LogParam
    @PostMapping("/udp/radio")
    public void udpRadio(@RequestBody CommandBO<String> command) {
        udpHandler.radio(String.valueOf(command.getData()));
    }

    @LogParam
    @PostMapping("/udp/upgrade")
    public ResponseEnvelope<CallableResult<String>> udpUpgrade(@RequestBody CommandBO<String> command) {
        return ResponseEnvelope.success(upgradeHandler.broadcastFile(command.getData(), System.currentTimeMillis()));
    }
}
