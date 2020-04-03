package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-20 19:28.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class DeviceBaseBO implements Serializable {
    private static final long serialVersionUID = 7222723385380290982L;
    /*** 帧头 */
    private String head;
    /*** 设备编号 */
    private String deviceNo;
    /*** 控制码 */
    private String code;
    /*** 数据域 */
    private String data;
    /*** 帧尾 */
    private String end;
    /*** crc校验码 */
    private String crc;
}
