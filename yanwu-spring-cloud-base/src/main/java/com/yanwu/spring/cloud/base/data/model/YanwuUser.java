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
@Table(name = "YANWU_USER", indexes = {@Index(name = "IX_YANWU_USER_ID", columnList = "ID")})
@EqualsAndHashCode(callSuper = true)
public class YanwuUser extends BaseMonopolyNamedBo implements Serializable {
    private static final long serialVersionUID = -5667178676337792115L;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "SEX")
    private Boolean sex;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "ROLE_ID")
    private Long roleId;

    @Column(name = "PORTRAIT")
    private Long portrait;
}
