package com.yanwu.spring.cloud.base.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 11:42.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("yanwu_role")
public class YanwuRole extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = -7133802858877953995L;

    /*** 角色名称 ***/
    @TableField("role_name")
    private String roleName;
    /*** 状态 ***/
    @TableField("status")
    private Boolean status;
}
