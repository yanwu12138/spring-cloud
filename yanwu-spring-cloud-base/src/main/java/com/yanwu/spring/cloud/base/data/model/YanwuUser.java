package com.yanwu.spring.cloud.base.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:35.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("base_user")
public class YanwuUser extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = -5667178676337792115L;
    /*** 账号 */
    @TableField("ACCOUNT")
    private String account;
    /*** 密码 */
    @TableField("PASSWORD")
    private String password;
    /*** 性别 */
    @TableField("SEX")
    private Boolean sex;
    /*** 手机号 */
    @TableField("PHONE")
    private String phone;
    /*** 邮箱 */
    @TableField("EMAIL")
    private String email;
    /*** 角色ID */
    @TableField("ROLE_ID")
    private Long roleId;
    /*** 头像 */
    @TableField("PORTRAIT")
    private Long portrait;
}
