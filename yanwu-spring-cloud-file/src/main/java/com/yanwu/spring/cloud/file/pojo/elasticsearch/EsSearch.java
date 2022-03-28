package com.yanwu.spring.cloud.file.pojo.elasticsearch;

import com.yanwu.spring.cloud.common.pojo.PageParam;
import com.yanwu.spring.cloud.common.utils.ObjectUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

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
    private EsType type;

    /*** 查询结果需要排除的字段 ***/
    private String[] excludes;

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

    /***
     * 处理查询参数
     * @return searchBuilder
     */
    public SearchSourceBuilder searchBuilder(Class<?> clazz) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        // ***** 可以根据字段进行搜索，must表示符合条件的，mustnot表示不符合条件的
        if (CollectionUtils.isNotEmpty(this.getData())) {
            this.getData().forEach((param) -> {
                if (param.getMust()) {
                    query.must(QueryBuilders.matchQuery(param.getField(), param.getValue()));
                } else {
                    query.mustNot(QueryBuilders.matchQuery(param.getField(), param.getValue()));
                }
            });
        }
        SearchSourceBuilder builder = new SearchSourceBuilder().query(query);
        // ***** 分页：获取记录数，默认为10
        builder.from(this.getPage()).size(this.getSize());
        // ***** 第一个参数是需要获取字段，第二个字段是需要排除的字段（默认获取所有字段）
        return builder.fetchSource(ObjectUtil.fieldNames(clazz), this.getExcludes());
    }

}
