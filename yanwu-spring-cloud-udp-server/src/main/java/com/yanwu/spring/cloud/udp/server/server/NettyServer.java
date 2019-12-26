package com.yanwu.spring.cloud.udp.server.server;

import com.yanwu.spring.cloud.udp.server.handler.ChannelHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-24 14:40.
 * <p>
 * description:
 */
@Slf4j
@Component
public class NettyServer {

    private Bootstrap bootstrap = new Bootstrap();
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    @Value("${netty.port}")
    private int nettyPort;
    @Resource
    private Executor nettyExecutor;

    @PostConstruct
    public void start() {
        nettyExecutor.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    bootstrap.group(bossGroup)
                            .channel(NioDatagramChannel.class)
                            .option(ChannelOption.SO_BROADCAST, true)
                            .handler(new ChannelHandler());
                    if (nettyPort < 1 || nettyPort > 65535) {
                        throw new RuntimeException("netty server start error, port is null!");
                    }
                    bootstrap.bind(nettyPort).sync().channel().closeFuture().await();
                    log.info("netty server start success, port: {}", nettyPort);
                }
            } catch (Exception e) {
                log.error("netty server start error: " + e);
            } finally {
                bossGroup.shutdownGracefully();
            }
        });
    }

    @PreDestroy
    public void close() {
        log.info("netty server is to stop ...");
        bossGroup.shutdownGracefully();
        log.info("netty server stop success!");
    }

}
