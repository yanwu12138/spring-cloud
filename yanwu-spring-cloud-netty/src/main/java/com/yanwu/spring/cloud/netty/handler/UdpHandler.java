package com.yanwu.spring.cloud.netty.handler;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.cache.ClientSessionCache;
import com.yanwu.spring.cloud.netty.config.NettyConfig;
import com.yanwu.spring.cloud.netty.enums.DeviceTypeEnum;
import com.yanwu.spring.cloud.netty.protocol.AbstractHandler;
import com.yanwu.spring.cloud.netty.protocol.DeviceHandlerFactory;
import com.yanwu.spring.cloud.netty.util.DeviceUtil;
import com.yanwu.spring.cloud.netty.util.NettyUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/21 14:53.
 * <p>
 * description:
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class UdpHandler extends ChannelInboundHandlerAdapter {
    private static final Object LOCK = new Object();

    @Resource
    private NettyConfig nettyConfig;
    @Resource
    private Executor nettyExecutor;
    @Resource
    private ClientSessionCache clientSessionCache;

    private static UdpHandler handler;
    private static volatile ChannelHandlerContext context;

    @PostConstruct
    public void init() {
        handler = this;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        initContext(ctx);
        DatagramPacket packet = (DatagramPacket) msg;
        String host = NettyUtil.getAddress(packet);
        clientSessionCache.putSocket(host, packet.sender());
        ByteBuf byteBuf = packet.copy().content();
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        handler.nettyExecutor.execute(() -> {
            log.info("read message, channel: {}, bytes: {}", host, ByteUtil.printBytes(bytes));
            // ----- 根据协议获取设备类型
            DeviceTypeEnum deviceType = DeviceUtil.getDeviceType(bytes);
            // ----- 根据设备类型获取对应的解析实现类
            AbstractHandler handler = DeviceHandlerFactory.getInstance(deviceType);
            // ----- 解析报文，业务处理
            Assert.notNull(handler, "handler is null");
            try {
                handler.analysis(host, bytes);
            } catch (Exception e) {
                log.error("udp analysis message error. message: {}", ByteUtil.printBytes(bytes), e);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        NettyUtil.close(ctx);
        log.error("netty udp error：", cause);
    }

    private void initContext(ChannelHandlerContext channelContext) {
        if (context != null) {
            return;
        }
        synchronized (LOCK) {
            if (context == null) {
                context = channelContext;
            }
        }
    }


    /**
     * 广播
     *
     * @param message 报文
     */
    public void radio(String message) {
        message = ByteUtil.gbkStrToHexStr(message);
        byte[] bytes = ByteUtil.hexStrToBytes(message);
        try (DatagramSocket socket = new DatagramSocket(nettyConfig.getRadioPort())) {
            java.net.InetAddress address = java.net.InetAddress.getByName("255.255.255.255");
            java.net.DatagramPacket packet = new java.net.DatagramPacket(bytes, bytes.length, address, nettyConfig.getRadioPort());
            socket.send(packet);
        } catch (Exception e) {
            log.error("udp radio error.", e);
        }
    }

    /**
     * 点播
     *
     * @param host    设备地址
     * @param message 报文
     */
    public void send(String host, String message) {
        InetSocketAddress socket = clientSessionCache.getSocket(host);
        if (socket == null || StringUtils.isBlank(message)) {
            return;
        }
        message = ByteUtil.gbkStrToHexStr(message);
        byte[] bytes = ByteUtil.hexStrToBytes(message);
        log.info("send message, channel: {}, message: {}", host, ByteUtil.printBytes(bytes));
        context.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(bytes), socket));
    }

}
