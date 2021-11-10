package com.yanwu.spring.cloud.netty.util;

import com.yanwu.spring.cloud.netty.enums.DeviceRegexEnum;
import com.yanwu.spring.cloud.netty.model.DeviceBaseBO;
import com.yanwu.spring.cloud.netty.model.alarmLamp.AlarmLampBaseBO;
import com.yanwu.spring.cloud.netty.model.screen.ScreenBaseBO;
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

    private ResolverUtil() {
        throw new UnsupportedOperationException("ResolverUtil should never be instantiated");
    }

    /**
     * 正则解析方法
     *
     * @param hexStr    16进制字符串（报文体）
     * @param regexEnum 协议对应的正则表达式
     * @return 对象
     */
    public static DeviceBaseBO regexParse(String hexStr, DeviceRegexEnum regexEnum) throws Exception {
        DeviceBaseBO instance = regexEnum.getClazz().newInstance();
        Pattern pattern = Pattern.compile(regexEnum.getRegex());
        Matcher matcher = pattern.matcher(hexStr.toUpperCase());
        if (matcher.find()) {
            Class<? extends DeviceBaseBO> clazz = regexEnum.getClazz();
            assignment(instance, clazz.getDeclaredFields(), matcher);
            assignment(instance, clazz.getSuperclass().getDeclaredFields(), matcher);
        }
        return instance;
    }

    private static void assignment(DeviceBaseBO instance, Field[] fields, Matcher matcher) {
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }
                field.set(instance, matcher.group(field.getName()));
            } catch (Exception e) {
                log.error("device regex parse error: ", e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // 帧头：1字节；设备编号：2字节；命令字：1字节；数据域N字节；帧尾：1字节；校验码2字节
        String hexStr = "AA000000002F3001A00113489D0BFE0B";
        ScreenBaseBO screen = (ScreenBaseBO) ResolverUtil.regexParse(hexStr, DeviceRegexEnum.SCREEN_REGEX);
        log.info("screen: {}", screen);
        hexStr = "484C00000000131420210123000101100207ABBF1B90DBF1FFA81413BF1B4C48";
        AlarmLampBaseBO alarmLamp = (AlarmLampBaseBO) ResolverUtil.regexParse(hexStr, DeviceRegexEnum.ALARM_LAMP_REGEX);
        log.info("alarm lamp: {}", alarmLamp);
    }

}
