package com.yanwu.spring.cloud.device.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanwu.spring.cloud.device.data.DeviceManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 15:52.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("device_pole")
public class DevicePole extends DeviceManager implements Serializable {
    private static final long serialVersionUID = -8121691962120028153L;
    /*** 高度 */
    @TableField("HEIGHT")
    private BigDecimal height;
    /*** 叉数 */
    @TableField("FORK_NUMBER")
    private BigDecimal forkNumber;
}
