package com.yanwu.spring.cloud.file.pojo.elasticsearch;

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
public class BaseType<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 5099977091556358817L;

    private String typeId;

    private T data;

}
