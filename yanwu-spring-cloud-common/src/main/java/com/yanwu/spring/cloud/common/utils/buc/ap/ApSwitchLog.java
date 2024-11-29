package com.yanwu.spring.cloud.common.utils.buc.ap;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/8/21 18:21.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ApSwitchLog implements Serializable {
    private static final long serialVersionUID = 4171105438547385521L;

    private Double lon;

    private Double lat;

    private String beamId;

    private String index;

    private String switchInfo;

    private String freq;

    private Long datetime;

}
