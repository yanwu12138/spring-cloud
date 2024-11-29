package com.yanwu.spring.cloud.common.utils.buc;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/11/25 15:12.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ShipOnlineLog implements Comparable<ShipOnlineLog>, Serializable {
    private static final long serialVersionUID = -3691069542007578915L;

    private String deviceSn;

    private Long time;

    private boolean online;

    public static ShipOnlineLog getInstance(String deviceSn, long time, boolean online) {
        return new ShipOnlineLog().setDeviceSn(deviceSn).setTime(time).setOnline(online);
    }

    @Override
    public int compareTo(ShipOnlineLog target) {
        return -this.getTime().compareTo(target.getTime());
    }

}
