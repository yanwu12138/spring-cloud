package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2018-11-07 16:41.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class PageParam<T> extends BaseParam<T> implements Serializable {
    private static final long serialVersionUID = -5028954569050562020L;

    private Integer size;

    private Integer page;

}
