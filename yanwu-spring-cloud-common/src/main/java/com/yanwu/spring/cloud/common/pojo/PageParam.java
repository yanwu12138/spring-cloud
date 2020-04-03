package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class PageParam extends BaseParam implements Serializable {
    private static final long serialVersionUID = -5028954569050562020L;

    private Integer size;

    private Integer page;

}
