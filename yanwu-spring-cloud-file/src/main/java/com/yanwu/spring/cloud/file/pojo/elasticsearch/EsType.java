package com.yanwu.spring.cloud.file.pojo.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2022/3/21 18:24.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class EsType implements Serializable {
    private static final long serialVersionUID = 5099977091556358817L;

    /*** 索引 ***/
    private EsIndex index;

    /*** 类型名称 ***/
    private String type;

    private String typeId;

    /*** 类型数据 ***/
    private JsonNode data;

}
