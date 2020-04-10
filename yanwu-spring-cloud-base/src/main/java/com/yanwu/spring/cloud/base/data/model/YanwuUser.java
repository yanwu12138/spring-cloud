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
@TableName("yanwu_user")
public class YanwuUser extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = -5667178676337792115L;
    /*** 账号 */
    @TableField("account")
    private String account;
    /*** 密码 */
    @TableField("password")
    private String password;
    /*** 性别 */
    @TableField("sex")
    private Boolean sex;
    /*** 手机号 */
    @TableField("phone")
    private String phone;
    /*** 邮箱 */
    @TableField("email")
    private String email;
    /*** 角色ID */
    @TableField("role_id")
    private Long roleId;
    /*** 头像 */
    @TableField("portrait")
    private Long portrait;
    /*** 状态 ***/
    @TableField("status")
    private Boolean status;
}
