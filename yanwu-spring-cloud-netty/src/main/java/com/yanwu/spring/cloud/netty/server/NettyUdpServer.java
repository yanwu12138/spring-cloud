package com.yanwu.spring.cloud.netty.server;

import com.yanwu.spring.cloud.netty.config.NettyConfig;
import com.yanwu.spring.cloud.netty.constant.Constants;
import com.yanwu.spring.cloud.netty.handler.UdpChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
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
    /*** 创建bootstrap */
    private Bootstrap bootstrap;
    /*** BOSS */
    private EventLoopGroup bossGroup;

    @Resource
    private NettyConfig nettyConfig;
    @Resource
    private Executor nettyExecutor;
    @Resource
    private UdpChannelHandler channelHandler;

    @PostConstruct
    public void start() {
        log.info("netty udp server starting ... port: {}", nettyConfig.getUdpPort());
        nettyExecutor.execute(() -> {
            try {
                if (nettyConfig.getUdpPort() < Constants.MIN_PORT || nettyConfig.getUdpPort() > Constants.MAX_PORT) {
                    throw new RuntimeException("netty udp server start error, port is illegal!");
                }
                bootstrap = new Bootstrap();
                bossGroup = new NioEventLoopGroup();
                while (!Thread.currentThread().isInterrupted()) {
                    bootstrap.group(bossGroup)
                            .channel(NioDatagramChannel.class)
                            .option(ChannelOption.SO_BACKLOG, 1024)
                            .option(ChannelOption.SO_RCVBUF, 8194)
                            .option(ChannelOption.SO_SNDBUF, 8194)
                            // ----- 支持广播
                            .option(ChannelOption.SO_BROADCAST, true)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .handler(channelHandler);
                    ChannelFuture future = bootstrap.bind(nettyConfig.getUdpPort()).sync();
                    future.channel().closeFuture().sync();
                }
            } catch (Exception e) {
                log.error("netty udp server start error: ", e);
            } finally {
                close();
            }
        });
    }

    /**
     * 停止服务
     */
    @PreDestroy
    public void close() {
        log.info("netty udp server is to stop ...");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        log.info("netty udp server stop success!");
    }

}
