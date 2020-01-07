package com.yanwu.spring.cloud.common.mvc.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;
import java.util.Map;

/**
 * @author Administrator
 */
@Data
@EqualsAndHashCode(of = {})
public abstract class BaseVo implements ValueObject {

    private static final long serialVersionUID = 5286126032962556646L;

    static public class IdComparator implements Comparator<BaseVo> {
        @Override
        public int compare(BaseVo vo1, BaseVo vo2) {
            if (vo1.getId() == null) {
                return vo2.getId() == null ? 0 : -1;
            }
            return vo2.getId() == null ? 1 : vo1.getId().compareTo(vo2.getId());
        }
    }

    private Long id;

    private Long createdAt;

    private Long updatedAt;

    private Map<String, Object> logMap;

}
