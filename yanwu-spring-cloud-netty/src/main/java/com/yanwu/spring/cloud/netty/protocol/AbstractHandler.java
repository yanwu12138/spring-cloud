package com.yanwu.spring.cloud.netty.protocol;

import com.yanwu.spring.cloud.netty.handler.TcpHandler;
import com.yanwu.spring.cloud.netty.model.DeviceBaseBO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:42.
 * <p>
 * description:
 */
@Component
public abstract class AbstractHandler {

    @Resource
    private TcpHandler tcpHandler;

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
    abstract public <T extends DeviceBaseBO> String assemble(T data) throws Exception;

    /**
     * 发送报文
     *
     * @param ctxId 通道号
     * @param data  报文
     * @throws Exception .
     */
    public <T extends DeviceBaseBO> void sendMessage(String ctxId, T data) throws Exception {
        tcpHandler.send(ctxId, assemble(data));
    }
}
