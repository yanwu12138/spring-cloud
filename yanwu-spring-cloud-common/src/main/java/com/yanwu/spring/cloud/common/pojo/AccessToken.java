package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3 17:59.
 * <p>
 * description:
 */
@Data
public class AccessToken implements Serializable {

    private Long id;

    private String account;

    private String expire;

}
