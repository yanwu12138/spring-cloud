package com.yanwu.spring.cloud.common.data.res;

import lombok.Data;

/**
 * @author XuBaofeng.
 * @date 2018-11-07 16:41.
 * <p>
 * description:
 */
@Data
public class PageInfoVO extends BaseVO {

    private Integer size;

    private Integer page;

}
