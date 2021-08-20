package com.yanwu.spring.cloud.netty.server;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.config.NettyConfig;
import com.yanwu.spring.cloud.netty.handler.SerialPortHandler;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

/**
 * @author Baofeng Xu
 * @date 2021/7/29 10:31.
 * <p>
 * description:
 */
@Slf4j
@Component
public class SerialPortServer {

    @Getter
    private SerialPort serialPort;

    @Resource
    private NettyConfig nettyConfig;
    @Resource
    private SerialPortHandler serialPortHandler;

    @PostConstruct
    public void init() {
        // ----- 获取系统中所有的通讯端口
        Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        boolean exists = false;
        while (portList.hasMoreElements()) {
            CommPortIdentifier commPortId = (CommPortIdentifier) portList.nextElement();
            //  ----- 判断是否是串口 && 比较串口名称是否是指定串口
            if (commPortId.getPortType() == CommPortIdentifier.PORT_SERIAL && nettyConfig.getSerialNumber().equals(commPortId.getName())) {
                exists = true;
                // ----- 打开串口
                try {
                    // ----- open:（应用程序名【随意命名】; 阻塞时等待的毫秒数）
                    serialPort = (SerialPort) commPortId.open("serial-port-channel", 2000);
                    // ----- 设置串口监听
                    serialPort.addEventListener(serialPortHandler);
                    // ----- 设置串口数据时间有效(可监听)
                    serialPort.notifyOnDataAvailable(true);
                    // ----- 设置串口通讯参数:波特率，数据位，停止位,校验方式
                    serialPort.setSerialPortParams(nettyConfig.getBaudRate(), nettyConfig.getDataBit(),
                            nettyConfig.getStopBit(), nettyConfig.getCheckoutBit());
                    log.info("serial port listener success. serialPort: {}", nettyConfig.getSerialNumber());
                } catch (PortInUseException e) {
                    throw new RuntimeException("The serial port is occupied");
                } catch (TooManyListenersException e) {
                    throw new RuntimeException("Too many listeners");
                } catch (UnsupportedCommOperationException e) {
                    throw new RuntimeException("Unsupported COMM port operation is abnormal");
                }
                // 结束循环
                break;
            }
        }
        // 若不存在该串口则抛出异常
        if (!exists) {
            log.error("this serial port does not exist. serialPort: {}", nettyConfig.getSerialNumber());
        }
    }

    /**
     * 发送信息到串口
     */
    public void sendComm(String data) {
        byte[] writerBuffer = ByteUtil.hexStrToBytes(data);
        try (OutputStream outputStream = serialPort.getOutputStream()) {
            outputStream.write(writerBuffer);
            outputStream.flush();
        } catch (NullPointerException e) {
            throw new RuntimeException("Can't find the serial port.");
        } catch (IOException e) {
            throw new RuntimeException("IO exception occurred when sending information to the serial port.");
        }
    }

    /**
     * 关闭串口
     */
    @PreDestroy
    public void closeSerialPort() {
        if (serialPort != null) {
            serialPort.notifyOnDataAvailable(false);
            serialPort.removeEventListener();
            serialPort.close();
            serialPort = null;
        }
    }
}
