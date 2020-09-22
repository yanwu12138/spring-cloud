package com.yanwu.spring.cloud.common.demo.d01java.d01proxy.d03cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;

/**
 * @author Baofeng Xu
 * @date 2020/9/22 14:28.
 * <p>
 * description: CGLIB动态代理工厂
 */
public class CglibProxyFactory {

    /**
     * 获取CGLIB动态代理对象
     *
     * @param clazz 源对象(被代理对象)
     * @return 代理对象
     */
    public static Object getProxy(Class<?> clazz) {
        return getProxy(clazz, new CglibProxyMethodInterceptor());
    }

    /**
     * 获取CGLIB动态代理对象
     *
     * @param clazz    源对象
     * @param callback 方法拦截器
     * @return 代理对象
     */
    public static Object getProxy(Class<?> clazz, Callback callback) {
        // ----- 创建动态代理增强类
        Enhancer enhancer = new Enhancer();
        // ----- 设置类加载器
        enhancer.setClassLoader(clazz.getClassLoader());
        // ----- 设置被代理类
        enhancer.setSuperclass(clazz);
        // ----- 设置方法拦截器
        enhancer.setCallback(callback);
        // ----- 创建代理类
        return enhancer.create();
    }
}
