package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.common.pojo.DeviceBaseBO;
import com.yanwu.spring.cloud.netty.constant.DeviceRegex;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 17:14.
 * <p>
 * description: 正则表达式解析工具类
 */
@Slf4j
public class ResolverUtil {

    /**
     * 正则解析方法
     *
     * @param hexStr 16进制字符串（报文体）
     * @param regex  正则表达式
     * @param clazz  对应正则解析之后的封装类
     * @return 对象
     */
    public static <T> T regexParse(String hexStr, String regex, Class<T> clazz) throws Exception {
        T obj = clazz.newInstance();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(hexStr.toUpperCase());
        if (matcher.find()) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(obj, matcher.group(field.getName()));
                } catch (Exception e) {
                    log.error("device regex parse error: ", e);
                }
            }
        }
        return obj;
    }

    public static void main(String[] args) throws Exception {
        // 帧头：1字节；设备编号：2字节；命令字：1字节；数据域N字节；帧尾：1字节；校验码2字节
        String hexStr = "AA2F3001A00113489D0BFE08";
        DeviceBaseBO device = ResolverUtil.regexParse(hexStr, DeviceRegex.Screen.SCREEN_NOVA_REGEX, DeviceBaseBO.class);
        log.info("device: {}", device);
    }

}
