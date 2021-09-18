package com.yanwu.spring.cloud.device.data.model;

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

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.VARCHAR;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 9:53.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("device_group")
@Table(name = "device_group", comment = "设备分组", charset = MySqlCharsetConstant.UTF8MB4)
public class DeviceGroup extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = -9038961024530589134L;

    /*** 组名 */
    @Unique
    @TableField("group_name")
    @Column(name = "group_name", type = VARCHAR, length = 64, isNull = false, comment = "组名")
    private String groupName;
}
