package com.yanwu.spring.cloud.common.mvc.vo.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 19:21.
 * <p>
 * description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YanwuUserVO {

    private Long id;

    private String name;

    private String userName;

    private Boolean sex;

    private String phone;

    private String email;

    private Long roleId;

    private String token;

    private Long portrait;
}
