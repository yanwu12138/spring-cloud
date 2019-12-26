package com.yanwu.spring.cloud.udp.server.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-24 17:21.
 * <p>
 * description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceChannel {
    private ChannelHandlerContext context;
    private DatagramPacket packet;
}
