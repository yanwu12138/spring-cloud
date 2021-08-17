package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * @author Baofeng Xu
 * @date 2021/8/16 11:31.
 * <p>
 * description:
 */
@Slf4j
@SuppressWarnings("unused")
public class CommandUtil {

    private CommandUtil() {
        throw new UnsupportedOperationException("CommandUtil should never be instantiated");
    }

    /**
     * 通过反射执行对应的函数
     *
     * @param clazz      源对象(被代理对象)
     * @param methodName 被代理方法
     * @param args       被代理方法参数集
     * @return 函数执行结果
     * @throws Exception Exception.class
     */
    public static Object invoke(Class<?> clazz, String methodName, Object... args) throws Exception {
        Method method = clazz.getDeclaredMethod(methodName, getArgType(args));
        Object result = method.invoke(ContextUtil.getBean(clazz), args);
        log.info("invoke. class: {}, method: {}, params: [{}], result: [{}]", clazz.getName(), method.getName(), args, result);
        return result;
    }

    /**
     * 执行SHELL命令
     *
     * @param cmd 命令脚本
     * @return 执行结果
     */
    public static String execCommand(String cmd) throws Exception {
        Process proc = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd});
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        IOUtil.close(reader);
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        while ((line = errorReader.readLine()) != null) {
            builder.append(line);
        }
        IOUtil.close(errorReader);
        proc.waitFor();
        log.info("exec shell command, cmd: {}, result: {}", cmd, builder);
        return builder.toString();
    }

    private static Class<?>[] getArgType(Object... args) {
        if (ArrayUtil.isEmpty(args)) {
            return new Class[]{};
        }
        Class<?>[] result = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = args[i].getClass();
        }
        return result;
    }

}
