package com.yanwu.spring.cloud.file.pojo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2021/1/21 15:42.
 * <p>
 * description:
 */
@Data
@Accessors
public class LuceneDocument implements Serializable {
    private static final long serialVersionUID = -494972534247054319L;

    private String title;

    private String content;

    private JSONObject json;

}
