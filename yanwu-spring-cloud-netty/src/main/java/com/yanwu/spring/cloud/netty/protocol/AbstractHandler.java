package com.yanwu.spring.cloud.netty.protocol;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:42.
 * <p>
 * description:
 */
public abstract class AbstractHandler {

    /**
     * 协议解析
     *
     * @param ctxId 通道号
     * @param bytes 报文
     */
    abstract public void analysis(String ctxId, byte[] bytes) throws Exception;

    /**
     * 发送报文
     *
     * @param ctxId 通道号
     * @param data  报文
     */
    abstract public <T> void sendMessage(String ctxId, T data);
}
