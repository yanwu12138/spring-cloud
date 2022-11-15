package com.yanwu.spring.cloud.netty.server;

import com.yanwu.spring.cloud.netty.config.NettyConfig;
import com.yanwu.spring.cloud.netty.constant.Constants;
import com.yanwu.spring.cloud.netty.handler.TcpChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:29.
 * <p>
 * description:
 */
@Slf4j
@Component
public class NettyTcpServer {
    private static final AtomicInteger ATOMIC_INDEX = new AtomicInteger(0);
    /*** 创建bootstrap */
    private ServerBootstrap bootstrap;
    /*** BOSS */
    private EventLoopGroup bossGroup;
    /*** Worker */
    private EventLoopGroup workGroup;


    @Resource
    private NettyConfig nettyConfig;
    @Resource
    private Executor nettyExecutor;
    @Resource
    private TcpChannelHandler channelHandler;

    @PostConstruct
    public void start() {
        log.info("netty tcp server starting ... port: {}", nettyConfig.getTcpPort());
        nettyExecutor.execute(() -> {
            try {
                if (nettyConfig.getTcpPort() < Constants.MIN_PORT || nettyConfig.getTcpPort() > Constants.MAX_PORT) {
                    throw new RuntimeException("netty udp server start error, port is illegal!");
                }
                bootstrap = new ServerBootstrap();
                bossGroup = new NioEventLoopGroup(1, r -> {
                    return new Thread(r, "netty-boos-" + ATOMIC_INDEX.getAndIncrement());
                });
                workGroup = new NioEventLoopGroup(nettyConfig.getServerWorker(), r -> {
                    return new Thread(r, "netty-worker-" + ATOMIC_INDEX.getAndIncrement());
                });
                while (!Thread.currentThread().isInterrupted()) {
                    bootstrap.group(bossGroup, workGroup)
                            .channel(NioServerSocketChannel.class)
                            .option(ChannelOption.SO_BACKLOG, 1024)
                            .option(ChannelOption.SO_RCVBUF, 8194)
                            .option(ChannelOption.SO_SNDBUF, 8194)
                            .option(ChannelOption.IP_TOS, 0xE0)
                            .childOption(ChannelOption.SO_KEEPALIVE, true)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .childHandler(channelHandler);
                    ChannelFuture future = bootstrap.bind(nettyConfig.getTcpPort()).sync();
                    future.channel().closeFuture().sync();
                }
            } catch (Exception e) {
                log.error("netty tcp server start error: ", e);
            } finally {
                close();
            }
        });
    }

    /**
     * 关闭服务器
     */
    @PreDestroy
    public void close() {
        log.info("netty tcp server is to stop ...");
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
        log.info("netty tcp server stop success!");
    }

}
