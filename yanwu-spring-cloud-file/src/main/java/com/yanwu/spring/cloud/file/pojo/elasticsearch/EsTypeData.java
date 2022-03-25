package com.yanwu.spring.cloud.file.pojo.elasticsearch;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2022/3/25 16:25.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class EsTypeData implements Serializable {
    private static final long serialVersionUID = -8714303989145525997L;
}
