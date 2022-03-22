package com.yanwu.spring.cloud.file.pojo.elasticsearch;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2022/3/21 17:23.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class BaseIndex<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 2602995042000556184L;

    private String index;

    private String type;

    private BaseType<T> typeData;

}
