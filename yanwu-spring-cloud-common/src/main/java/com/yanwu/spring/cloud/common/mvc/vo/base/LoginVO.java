package com.yanwu.spring.cloud.common.mvc.vo.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:37.
 * <p>
 * description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVO {

    private String account;

    private String password;

    private String captcha;

}
