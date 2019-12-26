package com.yanwu.spring.cloud.udp.server.handler;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import com.yanwu.spring.cloud.udp.server.cache.ClientSessionMap;
import com.yanwu.spring.cloud.udp.server.model.DeviceChannel;
import com.yanwu.spring.cloud.udp.server.util.DeviceUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 12:00.
 * <p>
 * description:
 */
@Slf4j
@Component
public class Handler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final byte[] SUCCESS = {0x48, 0x4c, 0x30, 0x31, 0x4c, 0x48};

    @Resource
    private Executor nettyExecutor;

    private static Handler handler;

    @PostConstruct
    public void init() {
        handler = this;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        byte[] data = ByteUtil.hexStr2ByteArr(packet.content().toString(CharsetUtil.UTF_8));
        if (!DeviceUtil.checkDevice(data)) {
            return;
        }
        String sn = DeviceUtil.getSn(data);
        DeviceChannel deviceChannel = new DeviceChannel(ctx, packet);
        ClientSessionMap.put(sn, deviceChannel);
        send(ctx, packet, ByteUtil.bytesToHexStr(SUCCESS));
    }

    public void sendMessage(String sn, String message) {
        DeviceChannel channel = ClientSessionMap.get(sn);
        if (Objects.isNull(channel) || Objects.isNull(channel.getContext()) || Objects.isNull(channel.getPacket())) {
            return;
        }
        send(channel.getContext(), channel.getPacket(), message);
    }

    private void send(ChannelHandlerContext ctx, DatagramPacket packet, String message) {
        ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8), packet.sender()));
    }

}