package com.yanwu.spring.cloud.device.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.yanwu.spring.cloud.device.data.DeviceManager;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.BIGINT;
import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.INT;

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
@Table(name = "device_light", comment = "单灯", charset = MySqlCharsetConstant.UTF8MB4, excludeFields = {"serialVersionUID", "groupId"})
public class DeviceLight extends DeviceManager implements Serializable {
    private static final long serialVersionUID = -4312299924027104281L;

    /*** 调光值 */
    @TableField("dimming")
    @Column(name = "dimming", type = INT, defaultValue = "0", comment = "调光值")
    private Integer dimming;

    /*** 电流 */
    @TableField("current")
    @Column(name = "current", type = INT, defaultValue = "0", comment = "电流")
    private Integer current;

    /*** 电压 */
    @TableField("voltage")
    @Column(name = "voltage", type = INT, defaultValue = "0", comment = "电压")
    private Integer voltage;

    /*** 功率 */
    @TableField("power")
    @Column(name = "power", type = INT, defaultValue = "0", comment = "功率")
    private Integer power;

    /*** 所属灯杆 */
    @TableField("pole_id")
    @Column(name = "pole_id", type = BIGINT, length = 20, isNull = false, comment = "所属灯杆ID")
    private Long poleId;

}
