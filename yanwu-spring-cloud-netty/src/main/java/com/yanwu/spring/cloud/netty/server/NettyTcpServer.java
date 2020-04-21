package com.yanwu.spring.cloud.netty.server;

import com.yanwu.spring.cloud.netty.handler.ChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:29.
 * <p>
 * description:
 */
@Slf4j
@Component
public class NettyTcpServer {
    /*** 创建bootstrap */
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    /*** BOSS */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    /*** Worker */
    private EventLoopGroup workGroup = new NioEventLoopGroup();

    @Value("${netty.port}")
    private int nettyPort;
    @Resource
    private Executor nettyExecutor;
    @Resource
    private ChannelHandler channelHandler;

    @PostConstruct
    public void start() {
        log.info("netty server starting ...");
        nettyExecutor.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    serverBootstrap.group(bossGroup, workGroup)
                            .channel(NioServerSocketChannel.class)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .option(ChannelOption.SO_BACKLOG, 1024)
                            .childOption(ChannelOption.SO_KEEPALIVE, true)
                            .childHandler(channelHandler);
                    if (nettyPort < 1 || nettyPort > 65535) {
                        throw new RuntimeException("netty server start error, port is null!");
                    }
                    ChannelFuture channel = serverBootstrap.bind(nettyPort).sync();
                    channel.channel().closeFuture().sync();
                    log.info("netty server start success, port: {}", nettyPort);
                }
            } catch (Exception e) {
                log.error("netty server start error: " + e);
            } finally {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        });
    }

    /**
     * 关闭服务器方法
     */
    @PreDestroy
    public void close() {
        log.info("netty server is to stop ...");
        //优雅退出
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
        log.info("netty server stop success!");
    }

}
