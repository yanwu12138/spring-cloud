package com.yanwu.spring.cloud.netty.protocol;

import com.yanwu.spring.cloud.netty.handler.TcpHandler;
import com.yanwu.spring.cloud.netty.handler.UdpHandler;
import com.yanwu.spring.cloud.netty.model.MessageQueueBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:42.
 * <p>
 * description:
 */
@Slf4j
@Component
public abstract class AbstractHandler {

    @Resource
    private TcpHandler tcpHandler;
    @Resource
    private UdpHandler udpHandler;

    /**
     * 协议解析
     *
     * @param ctxId 通道号
     * @param bytes 报文
     * @throws Exception .
     */
    abstract public void analysis(String ctxId, byte[] bytes) throws Exception;

    /**
     * 组装报文
     *
     * @param data 对象
     * @return 对象转报文
     * @throws Exception .
     */
    abstract public <T> String assemble(MessageQueueBO<T> data) throws Exception;

}
