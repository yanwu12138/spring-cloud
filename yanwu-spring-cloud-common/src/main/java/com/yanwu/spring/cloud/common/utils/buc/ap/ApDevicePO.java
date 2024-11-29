package com.yanwu.spring.cloud.common.utils.buc.ap;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/8/22 11:47.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ApDevicePO implements Serializable {
    private static final long serialVersionUID = -3406373030545104281L;

    private String apSn;

    private Long startTime;

    private Long endTime;

    public static ApDevicePO getInstance(String apSn, Long startTime, Long endTime) {
        return new ApDevicePO().setApSn(apSn).setStartTime(startTime).setEndTime(endTime);
    }

}
