package com.yanwu.spring.cloud.netty.util;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-08-09 13:10.
 * <p>
 * description: 正则表达式解析工具类
 */
public class ResolverUtil {

    /**
     * 正则解析方法
     *
     * @param hexStr 16进制字符串（报文体）
     * @param regex  正则表达式
     * @param object 对应正则解析之后的封装类
     * @return
     */
    public static Object regexParse(String hexStr, Object object, String regex) {
        try {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(hexStr.toUpperCase());
            if (m.find()) {
                Field[] field = object.getClass().getDeclaredFields();
                for (Field f : field) {
                    try {
                        f.setAccessible(true);
                        f.set(object, m.group(f.getName()));
                    } catch (Exception e) {

                    }
                }
            }
        } catch (Exception e) {
            object = null;
        }
        return object;
    }
}
