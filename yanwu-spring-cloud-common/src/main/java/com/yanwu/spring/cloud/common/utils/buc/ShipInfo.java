package com.yanwu.spring.cloud.common.utils.buc;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/11/22 15:37.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ShipInfo implements Serializable {
    private static final long serialVersionUID = 5924240660088817138L;

    private Long shipId;

    private String shipName;

    private Integer deviceType;

    private String deviceSn;

    private String modemType;

    private String bucTime;

}
