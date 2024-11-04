package com.yanwu.spring.cloud.netty.channels.server;

import com.yanwu.spring.cloud.common.core.common.Contents;
import com.yanwu.spring.cloud.netty.channels.handler.MulticastHandler;
import com.yanwu.spring.cloud.netty.config.NettyConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author Baofeng Xu
 * @date 2021/4/25 9:37.
 * <p>
 * description: 使用UDP进行文件组播
 */
@Slf4j
@Component
public class NettyMulticastServer {
    private static final int MAX_LENGTH = 1024 * 8 * 5;

    /*** 创建bootstrap */
    private Bootstrap bootstrap;
    /*** BOSS */
    private EventLoopGroup group;
    private InetSocketAddress groupSocketAddress;
    private NioDatagramChannel channel;

    @Resource
    private NettyConfig nettyConfig;
    @Resource
    private MulticastHandler multicastHandler;

    @PostConstruct
    public void start() {
        groupSocketAddress = new InetSocketAddress(nettyConfig.getBroadcastIp(), nettyConfig.getBroadcastPort());
        group = new NioEventLoopGroup(1, r -> {
            return new Thread(r, "multicast-upgrade-" + Contents.SEQ_NUM.getAndIncrement());
        });
        InetAddress localAddress = null;
        NetworkInterface loopbackIf = NetUtil.LOOPBACK_IF;
        Enumeration<InetAddress> addresses = loopbackIf.getInetAddresses();
        while (addresses.hasMoreElements()) {
            InetAddress address = addresses.nextElement();
            if (address instanceof Inet4Address) {
                localAddress = address;
            }
        }
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channelFactory((ChannelFactory<NioDatagramChannel>) () -> new NioDatagramChannel(InternetProtocolFamily.IPv4))
                .localAddress(localAddress, groupSocketAddress.getPort())
                .option(ChannelOption.IP_MULTICAST_IF, loopbackIf)
                .option(ChannelOption.IP_MULTICAST_TTL, 64)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    public void initChannel(NioDatagramChannel ch) {
                        ch.pipeline().addLast(multicastHandler);
                    }
                });
        try {
            channel = (NioDatagramChannel) bootstrap.bind(groupSocketAddress.getPort()).sync().channel();
            channel.joinGroup(groupSocketAddress, loopbackIf).sync();
        } catch (Exception e) {
            log.error("multicast upgrade start error.", e);
        } finally {
            close();
        }
    }

    /**
     * 关闭服务器
     */
    @PreDestroy
    public void close() {
        log.info("netty broadcast server is to stop ...");
        if (group != null) {
            group.shutdownGracefully();
        }
        log.info("netty broadcast server stop success!");
    }

    /**
     * 组播文件
     *
     * @param bytes 文件内容
     */
    public void broadcast(byte[] bytes) {
        if (bytes.length == 0 || bytes.length > MAX_LENGTH) {
            throw new RuntimeException("broadcast error. bytes is empty or too long");
        }
        try {
            ByteBuf byteBuf = Unpooled.buffer(bytes.length);
            byteBuf.writeBytes(bytes);
            channel.writeAndFlush(new DatagramPacket(byteBuf, groupSocketAddress)).sync();
        } catch (InterruptedException e) {
            log.error("broadcast error.", e);
        }
    }
}
