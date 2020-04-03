package com.yanwu.spring.cloud.base.vo;

import lombok.Data;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3 17:56.
 * <p>
 * description:
 */
@Data
public class YanwuUserVO {

    private Long id;
    private String account;
    private String description;
    private String phone;
    private String email;
    private Long roleId;
    private Boolean sex;
    private String token;

}
