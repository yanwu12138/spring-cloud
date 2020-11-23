package com.yanwu.spring.cloud.common.demo.d09socket.day004;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-11-23 22:53:00.
 * <p>
 * describe:
 */
@FunctionalInterface
public interface IHandlerInterface {

    void handler(Request request, Response response) throws Exception;

}
