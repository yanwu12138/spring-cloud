package com.yanwu.spring.cloud.netty.enums;

import com.yanwu.spring.cloud.netty.model.DeviceBaseBO;
import com.yanwu.spring.cloud.netty.model.alarmLamp.AlarmLampBaseBO;
import com.yanwu.spring.cloud.netty.model.screen.ScreenBaseBO;
import lombok.Getter;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:17.
 * <p>
 * description:
 */
public enum DeviceRegexEnum {

    /*** 显示屏正则表达式：【帧头：1字节；设备编号：2字节；命令字：1字节；数据域N字节；帧尾：1字节；校验码：2字节】 ***/
    SCREEN_REGEX("screen", "(?<head>\\w{2})(?<messageId>\\w{8})(?<deviceNo>\\w{4})(?<code>\\w{2})(?<data>\\w*)(?<end>\\w{2})(?<crc>\\w{4})", ScreenBaseBO.class),
    /*** 报警灯正则表达式：【帧头：2字节；SN：6字节；消息序列号：2字节；主命令字：1字节；子命令字：1字节；帧尾：2字节；校验码：2字节】 ***/
    ALARM_LAMP_REGEX("alarmLamp", "(?<head>\\w{4})(?<messageId>\\w{8})(?<sn>\\w{12})(?<seq>\\w{4})(?<mcode>\\w{2})(?<ccode>\\w{2})(?<data>\\w*)(?<crc>\\w{4})(?<end>\\w{4})", AlarmLampBaseBO.class),
    ;

    /*** 设备类型 ***/
    @Getter
    private final String type;
    /*** 设备的协议正则表达式 ***/
    @Getter
    private final String regex;
    /*** 设备协议解析出来的对象 ***/
    @Getter
    private final Class<? extends DeviceBaseBO> clazz;

    DeviceRegexEnum(String type, String regex, Class<? extends DeviceBaseBO> clazz) {
        this.type = type;
        this.regex = regex;
        this.clazz = clazz;
    }

}
