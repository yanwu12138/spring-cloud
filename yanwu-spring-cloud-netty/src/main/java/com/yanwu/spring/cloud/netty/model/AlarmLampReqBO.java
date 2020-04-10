package com.yanwu.spring.cloud.netty.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-19 14:37.
 * <p>
 * description:
 */
@Data
public class AlarmLampReqBO<T> implements Serializable {
    private static final long serialVersionUID = -7750707584959340095L;
    /*** 版本号 */
    private String ver;
    /*** 设备厂家 */
    private String fn;
    /*** 设备类型 */
    private String type;
    /*** 设备唯一标识 */
    private String sn;
    /*** 流水号 */
    private Integer seq;
    /*** 主命令字 */
    private String mcode;
    /*** 子命令字 */
    private String ccode;
    /*** 数据域 */
    private T data;
}