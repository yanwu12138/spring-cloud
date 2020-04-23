package com.yanwu.spring.cloud.base.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3 14:25.
 * <p>
 * description:
 */
@Data
public class LoginVO implements Serializable {

    @NotBlank(
            message = "账号不能为空"
    )
    @Size(
            min = 3,
            max = 30,
            message = "账号长度3-30字符"
    )
    @Pattern(
            regexp = "^[a-zA-Z0-9.@_]+$",
            message = "账号仅支持英文大小写字母，数字，和邮箱符号"
    )
    private String account;

    @NotBlank(
            message = "密码不能为空"
    )
    @Size(
            min = 6,
            max = 30,
            message = "密码长度6-30字符"
    )
    @Pattern(
            regexp = "^[a-zA-Z0-9-._@]+$",
            message = "密码仅支持英文大小写字母，数字，符号"
    )
    private String password;


}
