package com.yanwu.spring.cloud.netty.handler;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.cache.ClientSessionMap;
import com.yanwu.spring.cloud.netty.util.NettyUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 12:00.
 * <p>
 * description:
 */
@Slf4j
@Component
public class Handler extends ChannelInboundHandlerAdapter {

    @Resource
    Executor nettyExecutor;

    private static Handler handler;

    @PostConstruct
    public void init() {
        handler = this;
    }

    /**
     * 上行
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String ctxId = NettyUtils.getChannelId(ctx);
        ClientSessionMap.put(ctxId, ctx);
        byte[] bytes = (byte[]) msg;
        String message = ByteUtil.bytesToHexStrPrint(bytes);
        // ===== 处理上行业务
        handler.nettyExecutor.execute(() -> log.info("read message, channel: {}, message: {}", ctxId, message));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 断开连接
     *
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try {
            String ctxId = NettyUtils.getChannelId(ctx);
            if (ClientSessionMap.get(ctxId) == null) {
                return;
            }
            log.info("channel close connection, channel: {}", ctxId);
            ClientSessionMap.remove(ctxId);
            ctx.channel().close();
            ctx.close();
            // ===== 处理断线业务
        } catch (Exception e) {
            log.error("channel close error: ", e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("netty error：" + cause);
    }

    /**
     * 下行
     *
     * @param ctxId
     * @param message
     */
    public void send(String ctxId, String message) {
        message = message.replaceAll(" ", "");
        ChannelHandlerContext channel = ClientSessionMap.get(ctxId);
        if (channel == null || StringUtils.isBlank(message)) {
            return;
        }
        byte[] bytes = ByteUtil.strToHexBytes(message);
        log.info("send message, channel: {}, message: {}", ctxId, ByteUtil.bytesToHexStrPrint(bytes));
        channel.writeAndFlush(bytes);
    }

}