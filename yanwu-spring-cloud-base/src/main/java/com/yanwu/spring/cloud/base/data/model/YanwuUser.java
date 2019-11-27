package com.yanwu.spring.cloud.base.data.model;

import com.yanwu.spring.cloud.common.data.entity.BaseMonopolyNamedBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:35.
 * <p>
 * description:
 */
@Data
@Entity
@Table(name = "YANWU_USER", indexes = {
        @Index(name = "PK_YANWU_USER_ID", columnList = "ID"),
        @Index(name = "IDX_ROLE_ID", columnList = "ROLE_ID")
})
@EqualsAndHashCode(callSuper = true)
public class YanwuUser extends BaseMonopolyNamedBo implements Serializable {
    private static final long serialVersionUID = -5667178676337792115L;
    /*** 账号 */
    @Column(name = "ACCOUNT", unique = true, nullable = false)
    private String account;
    /*** 密码 */
    @Column(name = "PASSWORD", nullable = false)
    private String password;
    /*** 性别 */
    @Column(name = "SEX")
    private Boolean sex;
    /*** 手机号 */
    @Column(name = "PHONE")
    private String phone;
    /*** 邮箱 */
    @Column(name = "EMAIL")
    private String email;
    /*** 角色ID */
    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;
    /*** 头像 */
    @Column(name = "PORTRAIT")
    private Long portrait;
}
