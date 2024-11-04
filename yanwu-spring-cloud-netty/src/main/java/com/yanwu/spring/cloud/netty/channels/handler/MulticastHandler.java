package com.yanwu.spring.cloud.netty.channels.handler;

import com.yanwu.spring.cloud.common.pojo.Result;
import com.yanwu.spring.cloud.common.utils.FileUtil;
import com.yanwu.spring.cloud.netty.channels.server.NettyMulticastServer;
import com.yanwu.spring.cloud.netty.config.BroadcastExecutorService;
import com.yanwu.spring.cloud.netty.enums.BroadcastEnum;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author Baofeng Xu
 * @date 2021/4/25 11:31.
 * <p>
 * description:
 */
@Slf4j
@Component
@ChannelHandler.Sharable
@SuppressWarnings("unused")
public class MulticastHandler extends SimpleChannelInboundHandler<NioSocketChannel> {

    @Resource
    private BroadcastExecutorService executorService;
    @Resource
    private NettyMulticastServer multicastServer;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, NioSocketChannel msg) {
        log.warn("broadcast handler read, msg: {}", msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    public Result<String> broadcastFile(String filepath, Long magic) {
        if (executorService.isActive(BroadcastEnum.UPGRADE, String.valueOf(magic))) {
            return Result.failed("error");
        }
        return executorService.addRunnable(BroadcastEnum.UPGRADE, String.valueOf(magic), () -> {
            try {
                File file = new File(filepath);
                if (!FileUtil.fileExists(file) || !file.isFile()) {
                    return Result.failed("文件不存在或不是文件");
                }
                long length = file.length(), offset = 0;
                while (offset < length) {
                    int size = (int) Math.min(1024, length - offset);
                    multicastServer.broadcast(FileUtil.read(file.getPath(), offset, size));
                    offset += size;
                }
                return Result.success("success");
            } catch (Exception e) {
                log.error("broadcast upgrade addRunnable error.", e);
                return Result.failed("error");
            }
        });
    }

}
