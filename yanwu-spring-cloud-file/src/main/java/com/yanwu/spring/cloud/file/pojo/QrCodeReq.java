package com.yanwu.spring.cloud.file.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/9 10:43.
 * <p>
 * description:
 */
@Data
public class QrCodeReq implements Serializable {


    private String logoUrl;

    @NotBlank(message = "不能为空")
    private String content;

    private Integer timeout;

    @NotNull(message = "不能为空")
    private Integer lengthOfSide;

    private Integer logoLengthOfSide;

    private Boolean needCompress;

    @NotNull(message = "不能为空")
    private Long creator;

}
