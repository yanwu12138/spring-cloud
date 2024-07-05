package com.yanwu.spring.cloud.file.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2021/1/22 18:07.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class ZookeeperNode implements Serializable {
    private static final long serialVersionUID = -7270723293021118173L;

    private String path;

    private String value;

    public static ZookeeperNode newInstance(String path, String value) {
        return new ZookeeperNode().setPath(path).setValue(value);
    }
}
