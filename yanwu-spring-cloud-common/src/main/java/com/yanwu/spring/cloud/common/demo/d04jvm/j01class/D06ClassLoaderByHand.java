package com.yanwu.spring.cloud.common.demo.d04jvm.j01class;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-06-03 23:11:39.
 * <p>
 * describe:
 */
public class D06ClassLoaderByHand {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> aClass = D06ClassLoaderByHand.class.getClassLoader()
                .loadClass("com.yanwu.spring.cloud.common.demo.d04jvm.j01class.D06ClassLoaderByHand");
        System.out.println(aClass.getName());
    }
}
