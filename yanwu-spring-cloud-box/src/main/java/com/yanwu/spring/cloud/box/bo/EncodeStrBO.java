package com.yanwu.spring.cloud.box.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2024/3/25 09:26.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class EncodeStrBO implements Serializable {
    private static final long serialVersionUID = -2322321126619911612L;

    private String source;

    private String secret;

}
