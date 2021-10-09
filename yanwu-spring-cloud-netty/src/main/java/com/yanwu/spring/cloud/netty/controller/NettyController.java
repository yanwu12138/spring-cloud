package com.yanwu.spring.cloud.netty.controller;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.yanwu.spring.cloud.common.core.annotation.LogParam;
import com.yanwu.spring.cloud.common.pojo.CallableResult;
import com.yanwu.spring.cloud.common.pojo.CommandBO;
import com.yanwu.spring.cloud.common.pojo.ResponseEnvelope;
import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.common.utils.HttpUtil;
import com.yanwu.spring.cloud.common.utils.ThreadUtil;
import com.yanwu.spring.cloud.netty.handler.SerialPortHandler;
import com.yanwu.spring.cloud.netty.handler.TcpHandler;
import com.yanwu.spring.cloud.netty.handler.UdpHandler;
import com.yanwu.spring.cloud.netty.handler.UpgradeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
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
    public Callable<ResponseEnvelope<Void>> test() throws InterruptedException {
//        nettyExecutor.execute(this::runRadar);
//        nettyExecutor.execute(this::runAis);
        return () -> {
            Thread.sleep(10000);
            return ResponseEnvelope.success();
        };
    }

    /***
     * (id=744, formatVersion=1, utc=1630202093,
     * antennaSerial=HW000907N0085, gpsLocked=0,
     * muteState=1, gpsLongitudeDegs=0.0, gpsLatitudeDegs=0.0,
     * gpsSpeedKnots=0.0, gpsHeadingDegs=0.0,
     * faultListJson=["BUC current fault","GPS lost signal","ACU tracking unstable"],
     * mfActiveCarrierId=0, rxPolarisation=RXH, rxRfFreqKhz=1.125788E7,
     * rxLoFreqKhz=9750000.0, satLongitudeDegs=134.0, rxSymbolRateKsps=45000.0, openamipStateId=1,
     * demodConDbAvg=-10, trackingStable=0, temperatureDegc=54.89671, prVersion=2.4.4,
     * acuState=Waiting for necessary conditions, acuSubstate=Waiting, antState=Operating,
     * antSubstate=Initial, azPositionDegs=12.985, elPositionDegs=35.288, rlPositionDegs=3.513,
     * skPositionDegs=66.397, createTime=Sun Aug 29 09:54:57 CST 2021, openamipIp=172.28.86.114, openamipMask=255.255.255.248)
     */

    public static void main(String[] args) {
        runAntenna();
    }

    private static void runAntenna() {
        log.info("run antenna begin.");
        List<String> fault = new ArrayList<>();
        fault.add("BUC current fault");
        fault.add("GPS lost signal");
        fault.add("ACU tracking unstable");
        TAntennaStatus param = new TAntennaStatus().setId(744).setFormatVersion(1).setUtc(System.currentTimeMillis()).setAntennaSerial("HW000907N0085")
                .setGpsLocked(0).setMuteState(1).setGpsLongitudeDegs(0.0).setGpsLatitudeDegs(0.0).setGpsSpeedKnots(0.0).setGpsHeadingDegs(0.0)
                .setFaultList(fault).setMfActiveCarrierId(0).setRxPolarisation("RXH").setRxRfFreqKhz(11257880.0).setRxLoFreqKhz(9750000.0)
                .setSatLongitudeDegs(134.0).setRxSymbolRateKsps(45000.0).setOpenamipStateId(1).setDemodConDbAvg(-10.0).setTrackingStable(0)
                .setTemperatureDegc(54.89671).setPrVersion("2.4.4").setAcuState("Waiting for necessary conditions").setAcuSubstate("Waiting")
                .setAntState("Operating").setAntSubstate("Initial").setAzPositionDegs(12.985).setElPositionDegs(35.288).setRlPositionDegs(3.513)
                .setSkPositionDegs(66.397).setCreateTime(new Date()).setOpenamipIp("10.130.9.147").setOpenamipMask("255.255.255.248");
        try {
            int count = 120;
            while (count > 0) {
                param.setUtc(System.currentTimeMillis()).setCreateTime(new Date()).setDemodConDbAvg(5.0)
                        .setGpsLongitudeDegs(121.890002).setGpsLatitudeDegs(30.842547).setSatLongitudeDegs(134.0);
                Object result = HttpUtil.post("http://192.168.18.254:8001/api/amu/antennaStatus", param, Object.class);
                log.info("1 - count: {}, result: {}", count, result);
                count--;
                ThreadUtil.sleep(5_000);
            }
        } catch (Exception e) {
            log.error("run antenna failed.", e);
        }
        log.info("run antenna done.");
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


    @Data
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    private static class TAntennaStatus extends Model<TAntennaStatus> {
        private static final long serialVersionUID = 3764461082632587801L;
        private Integer id;
        private Integer formatVersion;
        private Long utc;
        private String antennaSerial;
        private Integer gpsLocked;
        private Integer muteState;
        private Double gpsLongitudeDegs;
        private Double gpsLatitudeDegs;
        private Double gpsSpeedKnots;
        private Double gpsHeadingDegs;
        private String faultListJson;
        private Integer mfActiveCarrierId;
        private String rxPolarisation;
        private Double rxRfFreqKhz;
        private Double rxLoFreqKhz;
        private Double satLongitudeDegs;
        private Double rxSymbolRateKsps;
        private Integer openamipStateId;
        private Double demodConDbAvg;
        private Integer trackingStable;
        private Double temperatureDegc;
        private String prVersion;
        private String acuState;
        private String acuSubstate;
        private String antState;
        private String antSubstate;
        private Double azPositionDegs;
        private Double elPositionDegs;
        private Double rlPositionDegs;
        private Double skPositionDegs;
        private Date createTime;
        private String openamipIp;
        private String openamipMask;
        private List<String> faultList;
    }

}
