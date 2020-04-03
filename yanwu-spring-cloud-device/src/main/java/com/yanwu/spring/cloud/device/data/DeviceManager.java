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
    @TableField("DEVICE_NO")
    private String deviceNo;
    /*** 安装地址 */
    @TableField("INSTALL_ADDRESS")
    private String installAddress;
    /*** 经度 */
    @TableField("LONGITUDE")
    private BigDecimal longitude;
    /*** 经度 */
    @TableField("LATITUDE")
    private BigDecimal latitude;
    /*** 分组 */
    @TableField("GROUP_ID")
    private Long groupId;
    /*** 状态 */
    @TableField("STATUS")
    private Integer status;
}
