package com.yanwu.spring.cloud.device.device.data.model;

import com.yanwu.spring.cloud.device.device.data.DeviceManager;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 15:46.
 * <p>
 * description:
 */
@Data
@Entity
@Table(name = "DEVICE_LIGHT", indexes = {
        @Index(name = "PK_DEVICE_LIGHT_ID", columnList = "ID"),
        @Index(name = "IDX_LIGHT_GROUP_ID", columnList = "GROUP_ID"),
        @Index(name = "IDX_LIGHT_POLE_ID", columnList = "POLE_ID")})
@EqualsAndHashCode(callSuper = true)
public class DeviceLight extends DeviceManager implements Serializable {
    private static final long serialVersionUID = -4312299924027104281L;
    /*** 调光值 */
    @Column(name = "DIMMING")
    private Integer dimming;
    /*** 电流 */
    @Column(name = "CURRENT")
    private Integer current;
    /*** 电压 */
    @Column(name = "VOLTAGE")
    private Integer voltage;
    /*** 功率 */
    @Column(name = "POWER")
    private Integer power;
    /*** 所属灯杆 */
    @Column(name = "POLE_ID")
    private Long poleId;
}
