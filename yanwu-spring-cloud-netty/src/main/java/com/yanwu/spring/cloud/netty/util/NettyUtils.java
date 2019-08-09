package com.yanwu.spring.cloud.netty.util;

import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 13:10.
 * <p>
 * description:
 */
public final class NettyUtils {
    private static final String SPLIT_PORT = ":";

    private NettyUtils() {
    }

    public static InetSocketAddress getRemoteAddress(ChannelHandlerContext ctx) {
        return (InetSocketAddress) ctx.channel().remoteAddress();
    }

    public static String getChannelId(ChannelHandlerContext ctx) {
        return ctx == null ? "" : ctx.channel().id().asLongText();
    }

    public static String getPort(ChannelHandlerContext ctx) {
        SocketAddress socketAddress = ctx.channel().localAddress();
        String localAddress = String.valueOf(socketAddress).trim().toUpperCase();
        if (StringUtils.isNotBlank(localAddress) && localAddress.contains(SPLIT_PORT)) {
            return localAddress.split(SPLIT_PORT)[1];
        }
        return null;
    }
}
