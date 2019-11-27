package com.yanwu.spring.cloud.device.device.data.model;

import com.yanwu.spring.cloud.common.data.entity.BaseMonopolyNamedBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 9:23.
 * <p>
 * description:
 */
@Data
@Entity
@Table(name = "DEVICE_MANAGER", indexes = {@Index(name = "IX_DEVICE_MANAGER_ID", columnList = "ID")})
@EqualsAndHashCode(callSuper = true)
public class DeviceManager extends BaseMonopolyNamedBo implements Serializable {
    private static final long serialVersionUID = 6137280284276233324L;

    @Column(name = "DEVICE_NO")
    private String deviceNo;
}
