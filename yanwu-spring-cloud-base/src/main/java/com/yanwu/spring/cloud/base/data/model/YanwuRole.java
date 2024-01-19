package com.yanwu.spring.cloud.base.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.yanwu.spring.cloud.common.core.annotation.DataScopeField;
import com.yanwu.spring.cloud.common.core.annotation.DataScopeTable;
import com.yanwu.spring.cloud.common.core.enums.AccessTypeEnum;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.TINYINT;
import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.VARCHAR;

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
@Table(name = "yanwu_role", comment = "角色表", charset = MySqlCharsetConstant.UTF8MB4)
@DataScopeTable(table = "yanwu_role", dataScope = @DataScopeField(field = "id", type = AccessTypeEnum.ROLE))
public class YanwuRole extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = -7133802858877953995L;

    /*** 角色名称 ***/
    @Unique
    @TableField("role_name")
    @Column(name = "role_name", type = VARCHAR, length = 32, isNull = false, comment = "角色名称")
    private String roleName;

    /*** 状态 ***/
    @TableField("status")
    @Column(name = "status", type = TINYINT, length = 1, isNull = false, defaultValue = "1", comment = "状态")
    private Boolean status;
}
