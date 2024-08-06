package com.yanwu.spring.cloud.common.test.temp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/8/6 12:09.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class DeviceInfo implements Serializable {
    private static final long serialVersionUID = 6925342692057735079L;
    private String beamId;
    private String index = "0";
    /*** 猫类型 ***/
    private String modemType;
    /*** 设备类型 ***/
    private String deviceType;
    /*** 设备序列号 ***/
    private String sn;
    /*** 切换时经度 ***/
    private Double lon;
    /*** 切换时纬度 ***/
    private Double lat;
    /*** 目标频点 ***/
    private Double freq;
    /*** 切换内容 ***/
    private String switchInfo;
    /*** 经纬度到锁定波束中心点的距离 ***/
    private Long distance;
    /*** 切换时间 ***/
    private Long lastTime;
    private String operator;
}
