package com.yanwu.spring.cloud.udp.client.comyanwuspringcloududpclient.client;

import com.yanwu.spring.cloud.common.utils.ByteUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-24 15:21.
 * <p>
 * description:
 */
public class NettyClient {
    private Bootstrap b = new Bootstrap();
    private EventLoopGroup group = new NioEventLoopGroup();
    private static byte[] bytes = {0x48, 0x4C, 0x47, 0x31, 0x32, 0x30, 0x31, 0x39, 0x31, 0x32, 0x32, 0x34, 0x30, 0x33, 0x31, 0x38, 0x38, 0x36, 0x4C, 0x48};
    private static final String IP = "255.255.255.255";
    private static final Integer PORT = 8888;

    public void run() throws Exception {
        try {
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChineseProverbClientHandler());
            Channel ch = b.bind(0).sync().channel();
            //向网段内的所有机器广播
            ch.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(ByteUtil.bytesToHexStr(bytes), CharsetUtil.UTF_8), new InetSocketAddress(IP, PORT))).sync();
            //客户端等待15s用于接收服务端的应答消息，然后退出并释放资源
            while (!ch.closeFuture().await(1000 * 60 * 60 * 24)) {
                System.out.println("查询超时！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            new NettyClient().run();
            Thread.sleep(1000 * 3);
        }
    }
}
