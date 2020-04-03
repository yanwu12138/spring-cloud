package com.yanwu.spring.cloud.device.device.vo;

import com.yanwu.spring.cloud.common.pojo.PageParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-24 11:19.
 * <p>
 * description:
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SearchDeviceVO extends PageParam implements Serializable {
    private static final long serialVersionUID = -1926901020895248999L;

    private String name;

}
