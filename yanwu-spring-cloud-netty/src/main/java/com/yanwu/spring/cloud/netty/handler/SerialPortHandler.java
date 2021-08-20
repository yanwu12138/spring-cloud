package com.yanwu.spring.cloud.netty.handler;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.server.SerialPortServer;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Baofeng Xu
 * @date 2021/7/29 10:48.
 * <p>
 * description:
 */
@Slf4j
@Component
public class SerialPortHandler implements SerialPortEventListener {

    @Resource
    private SerialPortServer serialPortServer;

    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            // ===== 解析串口读取的数据
            // ----- 通讯中断
            case SerialPortEvent.BI:
                // ----- 溢位错误
            case SerialPortEvent.OE:
                // ----- 帧错误
            case SerialPortEvent.FE:
                // ----- 奇偶校验错误
            case SerialPortEvent.PE:
                // ----- 载波检测
            case SerialPortEvent.CD:
                // ----- 清除发送
            case SerialPortEvent.CTS:
                // ----- 数据设备准备好
            case SerialPortEvent.DSR:
                // ----- 响铃侦测
            case SerialPortEvent.RI:
                // ----- 输出缓冲区已清空
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
            default:
                break;
            // ===== 有数据到达，调用读取数据的方法
            case SerialPortEvent.DATA_AVAILABLE:
                readMessage();
                break;
        }
    }

    /**
     * 读取串口数据
     */
    public void readMessage() {
        try (InputStream inputStream = serialPortServer.getSerialPort().getInputStream()) {
            byte[] bytes = new byte[inputStream.available()];
            while (inputStream.available() > 0) {
                inputStream.read(bytes);
            }
            log.info("reader message: {}", new String(bytes));
        } catch (Exception e) {
            log.error("read serial port message error.", e);
        }
    }

    /**
     * 发送信息到串口
     */
    public void sendMessage(String data) {
        byte[] writerBuffer = ByteUtil.strToAsciiBytes(data);
        try (OutputStream outputStream = serialPortServer.getSerialPort().getOutputStream()) {
            outputStream.write(writerBuffer);
            outputStream.flush();
            log.info("sender message: {}", data);
        } catch (NullPointerException e) {
            throw new RuntimeException("Can't find the serial port.");
        } catch (IOException e) {
            throw new RuntimeException("IO exception occurred when sending information to the serial port.");
        }
    }
}
