package com.yanwu.spring.cloud.udp.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 11:59.
 * <p>
 * description:
 */
public class ChannelHandler extends ChannelInitializer<NioDatagramChannel> {

    @Override
    protected void initChannel(NioDatagramChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(new ByteArrayDecoder());
        p.addLast(new ByteArrayEncoder());
        // ===== 增加业务处理handler
        p.addLast(new Handler());
    }

}