package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d02jdk;

import java.lang.reflect.Proxy;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 14:05.
 * <p>
 * description: JDK动态代理工厂
 */
public class JdkProxyFactory {

    /**
     * 获取代理对象
     *
     * @param target 源对象(被代理对象)
     * @return 代理对象
     */
    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new JdkProxyInvocationHandler(target)
        );
    }
}
