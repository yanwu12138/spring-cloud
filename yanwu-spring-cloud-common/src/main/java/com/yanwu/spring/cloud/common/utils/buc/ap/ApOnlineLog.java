package com.yanwu.spring.cloud.common.utils.buc.ap;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/8/21 18:51.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ApOnlineLog implements Serializable {
    private static final long serialVersionUID = -6788457573522556681L;

    private String event;

    private Long datetime;

}
