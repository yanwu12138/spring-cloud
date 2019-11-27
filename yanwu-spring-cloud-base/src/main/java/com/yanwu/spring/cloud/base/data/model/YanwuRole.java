package com.yanwu.spring.cloud.base.data.model;

import com.yanwu.spring.cloud.common.data.entity.BaseMonopolyNamedBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-11-27 11:42.
 * <p>
 * description:
 */
@Data
@Entity
@Table(name = "YANWU_ROLE", indexes = {
        @Index(name = "PK_YANWU_ROLE_ID", columnList = "ID")
})
@EqualsAndHashCode(callSuper = true)
public class YanwuRole extends BaseMonopolyNamedBo implements Serializable {
    private static final long serialVersionUID = -7133802858877953995L;

}
