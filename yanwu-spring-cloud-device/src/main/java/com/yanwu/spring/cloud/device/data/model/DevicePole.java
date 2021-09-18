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
import java.math.BigDecimal;

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.*;

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
@Table(name = "device_pole", comment = "灯杆", charset = MySqlCharsetConstant.UTF8MB4)
public class DevicePole extends DeviceManager implements Serializable {
    private static final long serialVersionUID = -8121691962120028153L;

    /*** 高度 */
    @TableField("height")
    @Column(name = "height", type = DOUBLE, length = 5, decimalLength = 2, isNull = false, comment = "高度")
    private BigDecimal height;

    /*** 叉数 */
    @TableField("fork_number")
    @Column(name = "fork_number", type = INT, length = 2, comment = "叉数")
    private Integer forkNumber;

    /*** 安装地址 */
    @TableField("install_address")
    @Column(name = "install_address", type = VARCHAR, isNull = false, comment = "安装地址")
    private String installAddress;

    /*** 经度 */
    @TableField("longitude")
    @Column(name = "longitude", type = DOUBLE, length = 10, decimalLength = 3, comment = "经度")
    private BigDecimal longitude;

    /*** 纬度 */
    @TableField("latitude")
    @Column(name = "latitude", type = DOUBLE, length = 10, decimalLength = 3, comment = "纬度")
    private BigDecimal latitude;
}
