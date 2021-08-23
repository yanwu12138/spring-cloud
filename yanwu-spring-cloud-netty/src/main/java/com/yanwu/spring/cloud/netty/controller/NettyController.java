package com.yanwu.spring.cloud.netty.controller;

import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.CallableResult;
import com.yanwu.spring.cloud.common.pojo.CommandBO;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import com.yanwu.spring.cloud.netty.handler.SerialPortHandler;
import com.yanwu.spring.cloud.netty.handler.TcpHandler;
import com.yanwu.spring.cloud.netty.handler.UdpHandler;
import com.yanwu.spring.cloud.netty.handler.UpgradeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

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

    @Resource
    private Executor nettyExecutor;
    @Resource
    private SerialPortHandler serialPortHandler;

    @LogParam
    @GetMapping("/test")
    public ResponseEnvelope<Void> test() {
        nettyExecutor.execute(this::runRadar);
        nettyExecutor.execute(this::runAis);
        return ResponseEnvelope.success();
    }

    private void runAis() {
        try {
            String filepath = "E:\\home\\ais-source-1.log";
            try (FileInputStream inputStream = new FileInputStream(filepath);
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String source;
                while ((source = bufferedReader.readLine()) != null) {
                    int length = Integer.parseInt(source.substring(7, 8));
                    int index = Integer.parseInt(source.substring(9, 10));
                    while (length != index) {
                        source = source + "\r\n" + bufferedReader.readLine();
                        index++;
                    }
                    serialPortHandler.sendMessage(source);
                    ThreadUtil.sleep(100);
                }
            }
        } catch (Exception e) {
            log.error("sender ais message error.", e);
        }
        log.info("sender message done.");
    }

    private void runRadar() {
        try {
            int port = 7102;
            String ip = "10.0.0.136";
            String filepath = "E:\\home\\sjhy-source.log";
            DatagramSocket socket = new DatagramSocket();
            try (FileInputStream inputStream = new FileInputStream(filepath);
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String source;
                while ((source = bufferedReader.readLine()) != null) {
                    byte[] bytes = ByteUtil.hexStrToBytes(source);
                    DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(ip), port);
                    socket.send(packet);
                    log.info("send sjhy message: {}", ByteUtil.printBytes(bytes));
                    ThreadUtil.sleep(100);
                }
            }
        } catch (Exception e) {
            log.error("sender sjhy message error.", e);
        }
        log.info("sender message done.");
    }

    public static void main(String[] args) {
        String filepath = "E:\\home\\27-radarData.log";
        String targetPath = "E:\\home\\radar1\\";
        try (FileInputStream inputStream = new FileInputStream(filepath);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String source;
            while ((source = bufferedReader.readLine()) != null) {
                if (!source.contains("radarData")) {
                    continue;
                }
                source = source.substring(33);
                Map map = JsonUtil.toObject(source, Map.class);
                List<Map> list = (List) map.get("radarTarget");
                if (CollectionUtils.isEmpty(list)) {
                    continue;
                }
                for (Map temp : list) {
                    String no = (String) temp.get("No");
                    String file = "no: " + no + "\t"
                            + "time: " + temp.get("stamp") + ", " + "\t"
                            + "bearing: " + temp.get("Bearing") + ", " + "\t"
                            + "distance: " + temp.get("Distance") + ", " + "\t"
                            + "cog: " + temp.get("Cog") + ", " + "\t"
                            + "sog: " + temp.get("Sog") + ", " + "\t"
                            + "lng: " + temp.get("Long") + ", " + "\t"
                            + "lat: " + temp.get("Lat") + "\r\n";
                    FileUtil.appendWrite(targetPath + no + ".log", file.getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (Exception e) {
            log.error("read radar data error.", e);
        }
    }

}
