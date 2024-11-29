package com.yanwu.spring.cloud.common.utils.buc.edge;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/11/25 15:05.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class EdgeOnlineLog implements Serializable {
    private static final long serialVersionUID = -2840754345844219449L;

    private Integer online;

    private String date_created;

}
