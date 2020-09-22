package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d03cglib;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 14:20.
 * <p>
 * description:
 */
@Slf4j
public class CglibProxyMethodInterceptor implements MethodInterceptor {

    /**
     * CGLIB 动态代理
     *
     * @param obj    被代理对象
     * @param method 被代理方法
     * @param args   被代理方法参数
     * @param proxy  用于调用原始方法
     * @return 返回值
     * @throws Throwable Throwable.class
     */
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        // ----- 调用方法之前，我们可以添加自己的操作
        log.info("before method " + method.getName());
        Object object = proxy.invokeSuper(obj, args);
        // ----- 调用方法之后，我们同样可以添加自己的操作
        log.info("after method " + method.getName());
        return object;
    }
}
