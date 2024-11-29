package com.yanwu.spring.cloud.common.utils.buc;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/11/29 10:52.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ShipOnlineDuration implements Serializable {
    private static final long serialVersionUID = 3259442613783430941L;

    private String shipName;

    private String deviceSn;

    private String bucTime;

    private long countOnline;

    private long lock6DTime;

    private long lock138Time;

    public static ShipOnlineDuration getInstance(String shipName, String deviceSn, String bucTime) {
        return new ShipOnlineDuration().setShipName(shipName).setDeviceSn(deviceSn).setBucTime(bucTime);
    }

}
