package com.yanwu.spring.cloud.file.pojo.elasticsearch;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2022/3/21 18:29.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class TestType implements Serializable {
    private static final long serialVersionUID = -5176754743886876772L;

    private String name;

    private Integer age;

    private Boolean sex;

    private String password;

}
