package com.yanwu.spring.cloud.device.device.data;

import com.yanwu.spring.cloud.common.data.entity.BaseMonopolyNamedBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 9:23.
 * <p>
 * description:
 */
@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class DeviceManager extends BaseMonopolyNamedBo implements Serializable {
    private static final long serialVersionUID = 6137280284276233324L;
    /*** 设备编号 */
    @Column(name = "DEVICE_NO", unique = true, nullable = false, length = DEFAULT_STRING_LENGTH)
    private String deviceNo;
    /*** 安装地址 */
    @Column(name = "INSTALL_ADDRESS", length = DEFAULT_LONG_STRING_LENGTH)
    private String installAddress;
    /*** 经度 */
    @Column(name = "LONGITUDE")
    private BigDecimal longitude;
    /*** 经度 */
    @Column(name = "LATITUDE")
    private BigDecimal latitude;
    /*** 分组 */
    @Column(name = "GROUP_ID")
    private Long groupId;
    /*** 状态 */
    @Column(name = "STATUS")
    private Integer status;
}
