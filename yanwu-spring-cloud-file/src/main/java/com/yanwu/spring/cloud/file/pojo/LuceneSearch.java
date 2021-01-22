package com.yanwu.spring.cloud.file.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 11:29.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class LuceneSearch implements Serializable {
    private static final long serialVersionUID = 3804762328678108991L;

    private String[] fields;

    private String value;

    private String sortField;

    private Boolean sort;

}
