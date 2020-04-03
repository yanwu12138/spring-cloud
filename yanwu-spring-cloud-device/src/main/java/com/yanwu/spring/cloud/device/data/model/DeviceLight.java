package com.yanwu.spring.cloud.device.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanwu.spring.cloud.device.data.DeviceManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 15:46.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("device_light")
public class DeviceLight extends DeviceManager implements Serializable {
    private static final long serialVersionUID = -4312299924027104281L;
    /*** 调光值 */
    @TableField("dimming")
    private Integer dimming;
    /*** 电流 */
    @TableField("current")
    private Integer current;
    /*** 电压 */
    @TableField("voltage")
    private Integer voltage;
    /*** 功率 */
    @TableField("power")
    private Integer power;
    /*** 所属灯杆 */
    @TableField("pole_id")
    private Long poleId;
}
