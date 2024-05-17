package com.yanwu.spring.cloud.postapi.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/5/17 14:59.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class RequestCacheBO implements Serializable {
    private static final long serialVersionUID = -958847495233921762L;

    private boolean type;

    private String name;

    private RequestInfo<?> requestInfo;

    private List<RequestCacheBO> children;

}
