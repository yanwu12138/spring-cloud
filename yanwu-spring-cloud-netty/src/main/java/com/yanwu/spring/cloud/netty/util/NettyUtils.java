package com.yanwu.spring.cloud.netty.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 13:10.
 * <p>
 * description:
 */
@Slf4j
public final class NettyUtils {
    private static final String SPLIT_PORT = ":";

    private NettyUtils() {
    }

    public static String getChannelId(ChannelHandlerContext ctx) {
        return ctx == null ? "" : ctx.channel().id().asLongText();
    }

    public static String getAddress(DatagramPacket packet) {
        if (packet == null || packet.sender() == null) {
            return null;
        }
        InetSocketAddress sender = packet.sender();
        return sender.getAddress().getHostAddress() + SPLIT_PORT + sender.getPort();
    }

    public static void close(ChannelHandlerContext ctx) {
        ctx.channel().close();
        ctx.close();
    }

    public static NetworkInterface getInterface(String interfaceName) {
        try {
            return NetworkInterface.getByName(interfaceName);
        } catch (SocketException e) {
            log.error("网卡: " + interfaceName + " SocketException", e);
        }
        return null;
    }
}
