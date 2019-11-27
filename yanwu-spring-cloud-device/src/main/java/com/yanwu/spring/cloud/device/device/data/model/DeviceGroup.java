package com.yanwu.spring.cloud.device.device.data.model;

import com.yanwu.spring.cloud.common.data.entity.BaseMonopolyNamedBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 9:53.
 * <p>
 * description:
 */
@Data
@Entity
@Table(name = "DEVICE_GROUP", indexes = {@Index(name = "IX_GROUP_ID", columnList = "ID")})
@EqualsAndHashCode(callSuper = true)
public class DeviceGroup extends BaseMonopolyNamedBo implements Serializable {
    private static final long serialVersionUID = -9038961024530589134L;

}
