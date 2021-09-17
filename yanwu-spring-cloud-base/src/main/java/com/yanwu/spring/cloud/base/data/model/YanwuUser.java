package com.yanwu.spring.cloud.base.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.*;

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
@Table(name = "yanwu_user", comment = "用户表", charset = MySqlCharsetConstant.UTF8MB4)
public class YanwuUser extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = -5667178676337792115L;
    /*** 账号 */
    @Unique
    @TableField("account")
    @Column(name = "account", type = VARCHAR, length = 32, isNull = false, comment = "账号")
    private String account;

    /*** 密码 */
    @TableField("password")
    @Column(name = "password", type = VARCHAR, isNull = false, comment = "密码")
    private String password;

    /*** 用户名称 ***/
    @TableField("user_name")
    @Column(name = "user_name", type = VARCHAR, length = 32, isNull = false, comment = "用户名称")
    private String roleName;

    /*** 性别 */
    @TableField("sex")
    @Column(name = "sex", type = TINYINT, length = 1, isNull = false, comment = "密码")
    private Boolean sex;

    /*** 手机号 */
    @TableField("phone")
    @Column(name = "phone", type = VARCHAR, length = 11, comment = "手机号")
    private String phone;

    /*** 邮箱 */
    @TableField("email")
    @Column(name = "email", type = VARCHAR, length = 128, comment = "邮箱")
    private String email;

    /*** 角色ID */
    @TableField("role_id")
    @Column(name = "role_id", type = BIGINT, length = 20, comment = "角色ID")
    private Long roleId;

    /*** 头像 */
    @TableField("portrait")
    @Column(name = "portrait", type = BIGINT, length = 20, comment = "头像")
    private Long portrait;

    /*** 状态 ***/
    @TableField("status")
    @Column(name = "status", type = TINYINT, length = 1, isNull = false, defaultValue = "1", comment = "状态")
    private Boolean status;
}
