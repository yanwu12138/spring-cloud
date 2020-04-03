package com.yanwu.spring.cloud.file.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author XuBaofeng.
 * @date 2018-11-09 14:56.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("attachment")
public class Attachment extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = 5026290395793755932L;
    /*** 关联id */
    @TableField("RELATION_ID")
    private Long relationId;
    /*** 文件名 */
    @TableField("NAME")
    private String name;
    /*** 附件地址 */
    @TableField("ATTACHMENT_ADDRESS")
    private String attachmentAddress;
    /*** 附件名称 */
    @TableField("ATTACHMENT_NAME")
    private String attachmentName;
    /*** 附件类型 */
    @TableField("ATTACHMENT_TYPE")
    private FileType attachmentType;
    /*** 附件大小 */
    @TableField("ATTACHMENT_SIZE")
    private Long attachmentSize;
}
