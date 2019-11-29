package com.yanwu.spring.cloud.device.device.data.model;

import com.yanwu.spring.cloud.device.device.data.DeviceManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 15:52.
 * <p>
 * description:
 */
@Data
@Entity
@Table(name = "DEVICE_POLE", indexes = {
        @Index(name = "PK_DEVICE_POLE_ID", columnList = "ID"),
        @Index(name = "IDX_POLE_GROUP_ID", columnList = "GROUP_ID")})
@EqualsAndHashCode(callSuper = true)
public class DevicePole extends DeviceManager implements Serializable {
    private static final long serialVersionUID = -8121691962120028153L;
    /*** 高度 */
    @Column(name = "HEIGHT")
    private BigDecimal height;
    /*** 叉数 */
    @Column(name = "FORK_NUMBER")
    private BigDecimal forkNumber;
}
