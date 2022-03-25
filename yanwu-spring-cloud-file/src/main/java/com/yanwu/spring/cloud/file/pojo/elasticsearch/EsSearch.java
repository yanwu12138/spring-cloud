package com.yanwu.spring.cloud.file.pojo.elasticsearch;

import com.yanwu.spring.cloud.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2022/3/25 17:31.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class EsSearch extends PageParam<List<EsSearch.SearchParam>> implements Serializable {
    private static final long serialVersionUID = 8284271227476313729L;

    /*** 类型 ***/
    private EsType<?> type;

    @Data
    @Accessors(chain = true)
    public static class SearchParam implements Serializable {
        private static final long serialVersionUID = -2862354486530853830L;

        /*** 字段 ***/
        private String field;
        /*** 查询值 ***/
        private String value;
        /*** 是否匹配 ***/
        private Boolean must;
    }

}
