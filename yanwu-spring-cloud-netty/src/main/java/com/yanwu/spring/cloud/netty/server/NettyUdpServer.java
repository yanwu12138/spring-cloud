package com.yanwu.spring.cloud.netty.server;

import com.yanwu.spring.cloud.common.core.enums.DeviceTypeEnum;
import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.protocol.factory.DeviceHandlerFactory;
import com.yanwu.spring.cloud.netty.protocol.up.AbstractHandler;
import com.yanwu.spring.cloud.netty.util.DeviceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Executor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/21 11:58.
 * <p>
 * description:
 */
@Slf4j
@Component
public class NettyUdpServer {

    private static DatagramSocket socket;
    private static final Integer DEFAULT_BYTE_SIZE = 2048;
    private static final byte ZERO = 0x00;

    @Value("${udp.port}")
    private int port;
    @Resource
    private Executor nettyExecutor;

    @PostConstruct
    public void init() throws Exception {
        // ===== 程序启动时初始化UDP监听Socket, 监听默认端口
        socket = new DatagramSocket(port);
        nettyExecutor.execute(() -> {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[DEFAULT_BYTE_SIZE], DEFAULT_BYTE_SIZE);
                while (!Thread.currentThread().isInterrupted()) {
                    // ----- 在初始化时指定读取数据的byte数组长度
                    socket.receive(packet);
                    // ----- 处理读取到的数据
                    readMessage(packet.getAddress().getHostAddress(), splitBytes(packet.getData(), packet.getLength()));
                }
            } catch (Exception e) {
                log.error("udp server error.", e);
            }
        });
    }

    /**
     * 解析设备报文
     *
     * @param host  设备地址
     * @param bytes 报文
     */
    private void readMessage(String host, byte[] bytes) {
        if (StringUtils.isBlank(host) || ArrayUtils.isEmpty(bytes)) {
            return;
        }
        nettyExecutor.execute(() -> {
            try {
                // ----- 根据协议获取设备类型
                DeviceTypeEnum deviceType = DeviceUtil.getDeviceType(bytes);
                // ----- 根据设备类型获取对应的解析实现类
                AbstractHandler handler = DeviceHandlerFactory.newInstance(deviceType);
                // ----- 解析报文，业务处理
                Assert.notNull(handler, "handler is null");
                handler.analysis(host, bytes);
                log.info("read message succeed, host: {}, bytes: {}", host, ByteUtil.bytesToHexStrPrint(bytes));
            } catch (Exception e) {
                log.error("read message error, host: {}, bytes: {}.", host, ByteUtil.bytesToHexStrPrint(bytes), e);
            }
        });
    }

    /**
     * 发送数据
     *
     * @param host  设备地址
     * @param bytes 报文
     */
    public void sendMessage(String host, byte[] bytes) {
        if (StringUtils.isBlank(host) || ArrayUtils.isEmpty(bytes)) {
            return;
        }
        nettyExecutor.execute(() -> {
            try {
                // ----- 确定发送方的IP地址及端口号，地址为本地网络
                InetAddress address = InetAddress.getByName(host);
                // ----- 创建发送类型的数据报
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
                // ----- 通过套接字发送数据
                socket.send(packet);
                log.info("send message succeed, host: {}, bytes: {}", host, ByteUtil.bytesToHexStrPrint(bytes));
            } catch (Exception e) {
                log.error("send message error, host: {}, bytes: {}.", host, ByteUtil.bytesToHexStrPrint(bytes), e);
            }
        });
    }


    /**
     * 处理报文，将报文后面所有的无意义的0x00都去掉
     *
     * @param bytes  报文
     * @param length 报文长度
     * @return 去除0X00的报文
     */
    private static byte[] splitBytes(byte[] bytes, int length) {
        byte[] result = new byte[length];
        System.arraycopy(bytes, 0, result, 0, length);
        return result;
    }

}
