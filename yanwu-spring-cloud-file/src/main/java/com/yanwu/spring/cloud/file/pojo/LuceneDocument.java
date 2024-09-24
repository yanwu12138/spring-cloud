package com.yanwu.spring.cloud.file.pojo;

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
@Accessors(chain = true)
public class LuceneDocument implements Serializable {
    private static final long serialVersionUID = -494972534247054319L;

    /*** 索引 ***/
    private String id;

    /*** 标题 ***/
    private String title;

    /*** 内容 ***/
    private String content;

    public static LuceneDocument getInstance(String id, String title, String content) {
        return new LuceneDocument().setId(id).setTitle(title).setContent(content);
    }

}
