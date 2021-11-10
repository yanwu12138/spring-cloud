package com.yanwu.spring.cloud.netty.model.alarmLamp;

import com.yanwu.spring.cloud.netty.model.DeviceBaseBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-19 14:37.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AlarmLampBaseBO extends DeviceBaseBO implements Serializable {
    private static final long serialVersionUID = -7750707584959340095L;

    /*** 帧头 */
    private String head;
    /*** 设备唯一标识 */
    private String sn;
    /*** 流水号 */
    private String seq;
    /*** 主命令字 */
    private String mcode;
    /*** 子命令字 */
    private String ccode;
    /*** 数据域 */
    private String data;
    /*** 帧尾 */
    private String end;
    /*** crc校验码 */
    private String crc;

}