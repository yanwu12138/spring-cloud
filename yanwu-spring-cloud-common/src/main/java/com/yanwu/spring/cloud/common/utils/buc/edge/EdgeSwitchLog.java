package com.yanwu.spring.cloud.common.utils.buc.edge;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/11/25 15:10.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class EdgeSwitchLog implements Serializable {
    private static final long serialVersionUID = 3434405070478105982L;

    private Double lon;

    private Double lat;

    private String beamId;

    private String index;

    private String switchInfo;

    private String freq;

    private Long datetime;
}
