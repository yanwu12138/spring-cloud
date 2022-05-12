package com.yanwu.spring.cloud.common.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.*;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020-03-26 18:41.
 * <p>
 * description:
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class BaseDo<PK extends Serializable> extends Model<BaseDo<PK>> implements Serializable {
    private static final long serialVersionUID = -5667178676337792115L;

    /*** 主键 ***/
    @Getter
    @Setter
    @TableId(value = "id", type = IdType.AUTO)
    @Column(name = "id", type = BIGINT, length = 20, isNull = false, comment = "主键ID", isKey = true, isAutoIncrement = true)
    private PK id;

    /*** 创建者 ***/
    @Getter
    @Setter
    @TableField(value = "creator", strategy = FieldStrategy.NOT_EMPTY)
    @Column(name = "creator", type = BIGINT, length = 20, comment = "创建者")
    private Long creator;

    /*** 创建时间 ***/
    @Getter
    @TableField(value = "created", strategy = FieldStrategy.NOT_EMPTY, fill = FieldFill.INSERT)
    @Column(name = "created", type = DATETIME, isNull = false, defaultValue = "CURRENT_TIMESTAMP", comment = "创建时间")
    private Timestamp created;

    /*** 更新者 ***/
    @Getter
    @Setter
    @TableField(value = "updator", strategy = FieldStrategy.IGNORED)
    @Column(name = "updator", type = BIGINT, length = 20, comment = "更新者")
    private Long updator;

    /*** 更新时间 ***/
    @Getter
    @Version
    @TableField(value = "updated", strategy = FieldStrategy.NOT_EMPTY, fill = FieldFill.INSERT_UPDATE)
    @Column(name = "updated", type = DATETIME, isNull = false, defaultValue = "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", comment = "更新时间")
    private Timestamp updated;

    /*** 启用禁用 ***/
    @Getter
    @Setter
    @TableField(value = "enabled", strategy = FieldStrategy.NOT_EMPTY, fill = FieldFill.INSERT)
    @Column(name = "enabled", type = TINYINT, length = 1, isNull = false, defaultValue = "1", comment = "更新时间")
    private Boolean enabled;

    /*** 描述 ***/
    @Getter
    @Setter
    @TableField(value = "description", strategy = FieldStrategy.IGNORED)
    @Column(name = "description", type = VARCHAR, comment = "描述")
    private String description;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    static public class LongIdComparator implements Comparator<BaseDo<Long>> {
        @Override
        public int compare(BaseDo<Long> o1, BaseDo<Long> o2) {
            if (o1.getId() == null) {
                return o2.getId() == null ? 0 : -1;
            }
            return o2.getId() == null ? 1 : o1.getId().compareTo(o2.getId());
        }
    }

}
