package com.yanwu.spring.cloud.common.utils.buc;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/11/25 15:13.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ShipSwitchLog implements Comparable<ShipSwitchLog>, Serializable {
    private static final long serialVersionUID = 1714878544911554128L;

    private String deviceSn;

    private Double lon;

    private Double lat;

    private String beamId;

    private String index;

    private String switchInfo;

    private String freq;

    private Long datetime;

    @Override
    public int compareTo(ShipSwitchLog target) {
        return -this.getDatetime().compareTo(target.getDatetime());
    }

}
