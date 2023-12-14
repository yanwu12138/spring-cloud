package com.yanwu.spring.cloud.netty.handler;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.common.utils.ContextUtil;
import com.yanwu.spring.cloud.netty.cache.ClientSessionCache;
import com.yanwu.spring.cloud.netty.enums.DeviceTypeEnum;
import com.yanwu.spring.cloud.netty.model.MessageQueueBO;
import com.yanwu.spring.cloud.netty.protocol.AbstractHandler;
import com.yanwu.spring.cloud.netty.protocol.DeviceHandlerFactory;
import com.yanwu.spring.cloud.netty.util.DeviceUtil;
import com.yanwu.spring.cloud.netty.util.NettyUtil;
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
    @Resource
    private ClientSessionCache clientSessionCache;

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
        String ctxId = NettyUtil.getChannelId(ctx);
        handler.clientSessionCache.putContext(ctxId, ctx);
        byte[] bytes = (byte[]) msg;
        // ===== 处理上行业务
        handler.nettyExecutor.execute(() -> {
            log.info("read message, channel: {}, message: {}", ctxId, ByteUtil.printBytes(bytes));
            // ----- 根据协议获取设备类型
            DeviceTypeEnum deviceType = DeviceUtil.getDeviceType(bytes);
            // ----- 根据设备类型获取对应的解析实现类
            AbstractHandler handler = DeviceHandlerFactory.newInstance(deviceType);
            // ----- 解析报文，业务处理
            Assert.notNull(handler, "handler is null");
            try {
                handler.analysis(ctxId, bytes);
            } catch (Exception e) {
                log.error("tcp analysis message error. message: {}", ByteUtil.printBytes(bytes), e);
            }
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
            String ctxId = NettyUtil.getChannelId(ctx);
            if (handler.clientSessionCache.getContext(ctxId) == null) {
                return;
            }
            log.info("channel close connection, channel: {}", ctxId);
            handler.clientSessionCache.remove(ctxId);
        } catch (Exception e) {
            log.error("channel close error: ", e);
        } finally {
            // ===== 处理断线业务
            NettyUtil.close(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx == null) {
            return;
        }
        String ctxId = NettyUtil.getChannelId(ctx);
        if (handler.clientSessionCache.getContext(ctxId) == null) {
            return;
        }
        handler.clientSessionCache.remove(ctxId);
        NettyUtil.close(ctx);
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
        ChannelHandlerContext channel = handler.clientSessionCache.getContext(ctxId);
        if (channel == null || StringUtils.isBlank(message)) {
            return;
        }
        byte[] bytes = ByteUtil.hexStrToBytes(message);
        log.info("send message, channel: {}, message: {}", ctxId, ByteUtil.printBytes(bytes));
        channel.writeAndFlush(bytes);
    }

    /**
     * 下行
     *
     * @param sn    设备唯一标识
     * @param queue 消息
     * @throws Exception .
     */
    public <T> void send(String sn, MessageQueueBO<T> queue) throws Exception {
        AbstractHandler abstractHandler = (AbstractHandler) ContextUtil.getBean(queue.getInstance());
        Assert.notNull(abstractHandler, "send message error, because handler is null.");
        send(handler.clientSessionCache.getDevice(sn), abstractHandler.assemble(queue));
    }

}