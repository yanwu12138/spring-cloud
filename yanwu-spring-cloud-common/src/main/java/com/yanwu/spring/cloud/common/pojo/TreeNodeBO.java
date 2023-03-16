package com.yanwu.spring.cloud.common.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Baofeng Xu
 * @date 2023-03-14 014 16:24:06.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class TreeNodeBO<T extends TreeNodeBO<T>> implements Serializable {
    private static final long serialVersionUID = -1202129183999958803L;

    private Long nodeId;

    private Long parentId;

    private List<T> child = new ArrayList<>();

}
