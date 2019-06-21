package com.yanwu.spring.cloud.common.mvc.vo.file;

import com.yanwu.spring.cloud.common.core.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author XuBaofeng.
 * @date 2018-11-12 15:37.
 * <p>
 * description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentVO {
    private Long id;
    private String name;
    private Long relationId;
    private String attachmentAddress;
    private String attachmentName;
    private FileType attachmentType;
    private Long attachmentSize;
    private Long createByUserId;
    private String createByUserName;
}
