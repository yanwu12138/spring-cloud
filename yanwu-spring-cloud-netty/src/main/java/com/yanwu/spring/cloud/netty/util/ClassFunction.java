package com.yanwu.spring.cloud.netty.util;

import java.lang.reflect.Method;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:42.
 * <p>
 * description:
 */
public class ClassFunction {
    private Object target;
    private Method targetMethod;

    public ClassFunction(Object target) {
        super();
        this.target = target;
    }

    public static Object function(Object cla, Method method, Object... objects) {
        ClassFunction cf = new ClassFunction(cla);
        cf.targetMethod = method;
        return cf.invoke(objects);
    }

    public Object invoke(Object... objects) {
        try {
            if (targetMethod == null) {
                return null;
            }
            targetMethod.setAccessible(true);
            return targetMethod.invoke(target, objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}