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

    /***
     * 通过反射执行对应的函数
     * @param clazz      源对象(被代理对象)
     * @param methodName 被代理方法
     * @param args       被代理方法参数集
     * @return 函数执行结果
     * @throws Exception Exception.class
     */
    public static Object invoke(Class<?> clazz, String methodName, Object... args) throws Exception {
        Method method = clazz.getDeclaredMethod(methodName, getArgType(args));
        Object object;
        if (ObjectUtil.isStatic(method)) {
            object = null;
        } else {
            object = ContextUtil.getBean(clazz);
            object = object == null ? clazz.newInstance() : object;
        }
        method.setAccessible(true);
        Object result = method.invoke(object, args);
        log.info("invoke. class: {}, method: {}, params: {}, result: [{}]", clazz.getSimpleName(), method.getName(), args, result);
        return result;
    }

    /***
     * 本地执行系统命令
     * @param cmd 命令脚本
     * @return 执行结果
     */
    public static String execCommand(String cmd) {
        String[] command;
        if (SystemUtil.isWindows()) {
            command = new String[]{"cmd", "/c", cmd};
        } else {
            command = new String[]{"/bin/sh", "-c", cmd};
        }
        BufferedReader reader = null, errorReader = null;
        InputStreamReader streamReader = null, errorStreamReader = null;
        try {
            Process proc = Runtime.getRuntime().exec(command);
            streamReader = new InputStreamReader(proc.getInputStream());
            reader = new BufferedReader(streamReader);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\r\n");
            }
            errorStreamReader = new InputStreamReader(proc.getErrorStream());
            errorReader = new BufferedReader(errorStreamReader);
            while ((line = errorReader.readLine()) != null) {
                builder.append(line).append("\r\n");
            }
            proc.waitFor();
            log.info("exec command success, cmd: {}", cmd);
            return builder.toString();
        } catch (Exception e) {
            log.error("exec command failed. cmd: {}.", cmd, e);
            return null;
        } finally {
            IOUtil.closes(errorReader, errorStreamReader, reader, streamReader);
        }
    }

    /***
     * 获取参数集对应的类型集
     */
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
