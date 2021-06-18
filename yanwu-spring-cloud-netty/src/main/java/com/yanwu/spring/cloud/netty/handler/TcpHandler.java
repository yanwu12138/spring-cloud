package com.yanwu.spring.cloud.netty.handler;

import com.yanwu.spring.cloud.common.core.enums.DeviceTypeEnum;
import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.netty.cache.ClientSessionMap;
import com.yanwu.spring.cloud.netty.protocol.factory.DeviceHandlerFactory;
import com.yanwu.spring.cloud.netty.protocol.up.AbstractHandler;
import com.yanwu.spring.cloud.netty.util.DeviceUtil;
import com.yanwu.spring.cloud.netty.util.NettyUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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
@SuppressWarnings("unused")
public class TcpHandler extends ChannelInboundHandlerAdapter {

    @Resource
    private Executor nettyExecutor;

    private static TcpHandler handler;

    @PostConstruct
    public void init() {
        handler = this;
    }

    /**
     * 上行
     *
     * @param ctx 通道
     * @param msg 报文
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String ctxId = NettyUtils.getChannelId(ctx);
        ClientSessionMap.putContext(ctxId, ctx);
        byte[] bytes = (byte[]) msg;
        // ===== 处理上行业务
        handler.nettyExecutor.execute(() -> {
            log.info("read message, channel: {}, message: {}", ctxId, ByteUtil.printHexStrByBytes(bytes));
            // ----- 根据协议获取设备类型
            DeviceTypeEnum deviceType = DeviceUtil.getDeviceType(bytes);
            // ----- 根据设备类型获取对应的解析实现类
            AbstractHandler handler = DeviceHandlerFactory.newInstance(deviceType);
            // ----- 解析报文，业务处理
            Assert.notNull(handler, "handler is null");
            handler.analysis(ctxId, bytes);
        });
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * 断开连接
     *
     * @param ctx 通道号
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }
        try {
            String ctxId = NettyUtils.getChannelId(ctx);
            if (ClientSessionMap.getContext(ctxId) == null) {
                return;
            }
            log.info("channel close connection, channel: {}", ctxId);
            ClientSessionMap.remove(ctxId);
        } catch (Exception e) {
            log.error("channel close error: ", e);
        } finally {
            // ===== 处理断线业务
            NettyUtils.close(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx == null) {
            return;
        }
        String ctxId = NettyUtils.getChannelId(ctx);
        if (ClientSessionMap.getContext(ctxId) == null) {
            return;
        }
        ClientSessionMap.remove(ctxId);
        NettyUtils.close(ctx);
        log.error("netty tcp error：", cause);
    }

    /**
     * 下行
     *
     * @param ctxId   通道号
     * @param message 报文
     */
    public void send(String ctxId, String message) {
        message = message.replaceAll(" ", "");
        ChannelHandlerContext channel = ClientSessionMap.getContext(ctxId);
        if (channel == null || StringUtils.isBlank(message)) {
            return;
        }
        byte[] bytes = ByteUtil.hexStrToBytes(message);
        log.info("send message, channel: {}, message: {}", ctxId, ByteUtil.printHexStrByBytes(bytes));
        channel.writeAndFlush(bytes);
    }

}