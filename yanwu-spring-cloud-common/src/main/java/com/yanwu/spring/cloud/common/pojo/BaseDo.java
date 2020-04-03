package com.yanwu.spring.cloud.common.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020-03-26 18:41.
 * <p>
 * description:
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BaseDo<PK extends Serializable> extends Model implements Serializable {
    private static final long serialVersionUID = -5667178676337792115L;

    /*** 主键 ***/
    @Getter
    @Setter
    @TableId(value = "id", type = IdType.AUTO)
    private PK id;

    /*** 创建者 ***/
    @Getter
    @Setter
    @TableField(value = "creator", strategy = FieldStrategy.NOT_EMPTY)
    private Long creator;

    /*** 创建时间 ***/
    @Getter
    @TableField(value = "created", strategy = FieldStrategy.NOT_EMPTY, fill = FieldFill.INSERT)
    private Timestamp created;

    /*** 更新者 ***/
    @Getter
    @Setter
    @TableField(value = "updator", strategy = FieldStrategy.IGNORED)
    private Long updator;

    /*** 更新时间 ***/
    @Getter
    @Version
    @TableField(value = "updated", strategy = FieldStrategy.NOT_EMPTY, fill = FieldFill.INSERT_UPDATE)
    private Timestamp updated;

    /*** 启用禁用 ***/
    @Getter
    @Setter
    @TableField(value = "enabled", strategy = FieldStrategy.NOT_EMPTY, fill = FieldFill.INSERT)
    private Boolean enabled;

    /*** 描述 ***/
    @Getter
    @Setter
    @TableField(value = "description", strategy = FieldStrategy.IGNORED)
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
