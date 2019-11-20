package com.yanwu.spring.cloud.netty.protocol.up;

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
     * @param bytes
     */
    abstract public void analysis(byte[] bytes);

}
