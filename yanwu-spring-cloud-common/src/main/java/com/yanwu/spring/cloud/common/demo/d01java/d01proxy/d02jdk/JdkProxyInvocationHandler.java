package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d02jdk;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 14:02.
 * <p>
 * description:
 */
@Slf4j
public class JdkProxyInvocationHandler implements InvocationHandler {

    private final Object target;

    public JdkProxyInvocationHandler(Object target) {
        this.target = target;
    }

    /**
     * JDK动态代理
     *
     * @param proxy  被代理对象
     * @param method 被代理方法
     * @param args   被代理方法参数
     * @return 返回值
     * @throws Throwable Throwable.class
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // ----- 调用方法之前，我们可以添加自己的操作
        log.info("before method " + method.getName());
        Object result = method.invoke(target, args);
        // ----- 调用方法之后，我们同样可以添加自己的操作
        log.info("after method " + method.getName());
        return result;
    }
}
