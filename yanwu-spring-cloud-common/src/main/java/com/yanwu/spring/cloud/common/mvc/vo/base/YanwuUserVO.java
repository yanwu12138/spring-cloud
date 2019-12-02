package com.yanwu.spring.cloud.common.mvc.vo.base;

import com.yanwu.spring.cloud.common.core.annotation.CheckFiled;
import com.yanwu.spring.cloud.common.core.aspect.CheckParamRegex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 19:21.
 * <p>
 * description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YanwuUserVO implements Serializable {
    private static final long serialVersionUID = -524469592634576449L;

    private Long id;

    @CheckFiled(regex = CheckParamRegex.STRING_NOT_NULL, message = "用户名格式不匹配，请重新输入")
    private String name;

    @CheckFiled(regex = CheckParamRegex.STRING_NOT_NULL, message = "账户格式不匹配，请重新输入")
    private String account;

    private Boolean sex;

    @CheckFiled(regex = CheckParamRegex.PHONE_NO, message = "手机号码格式不匹配，请重新输入")
    private String phone;

    @CheckFiled(regex = CheckParamRegex.EMAIL, message = "邮箱格式不匹配，请重新输入")
    private String email;

    private Long roleId;

    private String token;

    private Long portrait;
}
