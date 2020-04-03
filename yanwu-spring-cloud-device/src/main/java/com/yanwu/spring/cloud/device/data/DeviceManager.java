package com.yanwu.spring.cloud.device.data;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

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
    @TableField("device_no")
    private String deviceNo;
    /*** 安装地址 */
    @TableField("install_address")
    private String installAddress;
    /*** 经度 */
    @TableField("longitude")
    private BigDecimal longitude;
    /*** 经度 */
    @TableField("latitude")
    private BigDecimal latitude;
    /*** 分组 */
    @TableField("groupId")
    private Long groupId;
    /*** 状态 */
    @TableField("status")
    private Integer status;
}
