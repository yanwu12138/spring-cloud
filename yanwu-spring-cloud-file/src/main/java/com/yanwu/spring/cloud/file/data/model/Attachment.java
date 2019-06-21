package com.yanwu.spring.cloud.file.data.model;

import com.yanwu.spring.cloud.common.core.enums.FileType;
import com.yanwu.spring.cloud.common.data.entity.BaseMonopolyNamedBo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 * @author XuBaofeng.
 * @date 2018-11-09 14:56.
 * <p>
 * description:
 */
@Data
@Entity
@Table(name = "ATTACHMENT", indexes = {@Index(name = "IX_ATTACHMENT_ID", columnList = "ID")})
@EqualsAndHashCode(callSuper = true)
public class Attachment extends BaseMonopolyNamedBo {

    /**
     * 关联id
     */
    @Column(name = "RELATION_ID")
    private Long relationId;

    /**
     * 附件地址
     */
    @Column(name = "ATTACHMENT_ADDRESS")
    private String attachmentAddress;

    /**
     * 附件名称
     */
    @Column(name = "ATTACHMENT_Name")
    private String attachmentName;

    /**
     * 附件类型
     */
    @Column(name = "ATTACHMENT_TYPE")
    private FileType attachmentType;

    /**
     * 附件大小
     */
    @Column(name = "ATTACHMENT_SIZE")
    private Long attachmentSize;

    /**
     * 创建人
     */
    @Column(name = "CREATE_BY_USER_ID")
    private Long createByUserId;

    /**
     * 创建人
     */
    @Column(name = "CREATE_BY_USER_Name")
    private String createByUserName;
}
