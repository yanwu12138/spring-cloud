package com.yanwu.spring.cloud.device.data;

import com.baomidou.mybatisplus.annotation.TableField;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 9:23.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DeviceManager extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = 6137280284276233324L;

    /*** 设备编号 */
    @Unique
    @TableField("device_no")
    @Column(name = "device_no", type = VARCHAR, length = 64, isNull = false, comment = "设备编号")
    private String deviceNo;

    /*** 设备名称 */
    @Unique
    @TableField("device_name")
    @Column(name = "device_name", type = VARCHAR, length = 64, isNull = false, comment = "设备名称")
    private String deviceName;

    /*** 分组 */
    @TableField("group_id")
    @Column(name = "group_id", type = BIGINT, length = 20, comment = "设备所属分组ID")
    private Long groupId;

    /*** 状态 */
    @TableField("status")
    @Column(name = "status", type = TINYINT, length = 1, isNull = false, defaultValue = "1", comment = "状态")
    private Boolean status;

}
