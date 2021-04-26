package com.yanwu.spring.cloud.netty.server;

import com.yanwu.spring.cloud.netty.config.NettyConfig;
import com.yanwu.spring.cloud.netty.handler.UpgradeHandler;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Baofeng Xu
 * @date 2021/4/25 9:37.
 * <p>
 * description: 使用UDP进行文件组播
 */
@Slf4j
@Component
public class BroadcastServer {
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
    private UpgradeHandler upgradeHandler;

    @PostConstruct
    public void start() {
        groupSocketAddress = new InetSocketAddress(nettyConfig.getBroadcastIp(), nettyConfig.getBroadcastPort());
        group = new NioEventLoopGroup(1, new ThreadFactory() {
            private final AtomicInteger index = new AtomicInteger(0);

            @Override
            public Thread newThread(@NonNull Runnable r) {
                return new Thread(r, "multicast-upgrade-" + index.getAndIncrement());
            }
        });
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channelFactory((ChannelFactory<NioDatagramChannel>) () -> new NioDatagramChannel(InternetProtocolFamily.IPv4))
                .option(ChannelOption.IP_MULTICAST_IF, nettyConfig.getInter())
                .option(ChannelOption.IP_MULTICAST_TTL, 64)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    public void initChannel(NioDatagramChannel ch) {
                        ch.pipeline().addLast(upgradeHandler);
                    }
                });
        try {
            InetAddress sourceInetAddress = Inet4Address.getByName(nettyConfig.getSourceIp());
            channel = (NioDatagramChannel) bootstrap.bind(sourceInetAddress, 0).syncUninterruptibly().channel();
            channel.joinGroup(groupSocketAddress.getAddress(), nettyConfig.getInter(), sourceInetAddress).sync();
        } catch (Exception e) {
            log.error("multicast upgrade start error.", e);
            throw new RuntimeException(e);
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
