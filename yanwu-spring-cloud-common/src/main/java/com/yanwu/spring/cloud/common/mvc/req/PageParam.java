package com.yanwu.spring.cloud.common.mvc.req;

import lombok.Data;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2018-11-07 16:41.
 * <p>
 * description:
 */
@Data
public class PageParam extends BaseParam implements Serializable {
    private static final long serialVersionUID = -5028954569050562020L;

    private Integer size;

    private Integer page;

}
