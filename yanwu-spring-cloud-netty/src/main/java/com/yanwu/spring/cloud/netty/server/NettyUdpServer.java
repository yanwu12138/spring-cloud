package com.yanwu.spring.cloud.netty.server;

import com.yanwu.spring.cloud.netty.handler.UdpChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
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
 * @date 2020/4/21 11:58.
 * <p>
 * description:
 */
@Slf4j
@Component
public class NettyUdpServer {
    /*** 创建bootstrap */
    private Bootstrap bootstrap = new Bootstrap();
    /*** BOSS */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    @Value("${udp.port}")
    private int port;
    @Resource
    private Executor nettyExecutor;
    @Resource
    private UdpChannelHandler channelHandler;

    @PostConstruct
    public void start() {
        log.info("netty udp server starting ... port: {}", port);
        nettyExecutor.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    bootstrap.group(bossGroup)
                            .channel(NioDatagramChannel.class)
                            .option(ChannelOption.SO_BACKLOG, 1024)
                            // ----- 支持广播
                            .option(ChannelOption.SO_BROADCAST, true)
                            .handler(new LoggingHandler(LogLevel.INFO))
                            .handler(channelHandler);
                    if (port < 1 || port > 65535) {
                        throw new RuntimeException("netty udp server start error, port is null!");
                    }
                    bootstrap.bind(port).sync().channel().closeFuture().sync();
                }
            } catch (Exception e) {
                log.error("netty udp server start error: " + e);
            } finally {
                close();
            }
        });
    }

    /**
     * 关闭服务器方法
     */
    @PreDestroy
    public void close() {
        log.info("netty udp server is to stop ...");
        bossGroup.shutdownGracefully();
        log.info("netty udp server stop success!");
    }

}
