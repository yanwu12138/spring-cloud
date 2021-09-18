package com.yanwu.spring.cloud.file.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.*;

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
@Table(name = "attachment", comment = "附件表", charset = MySqlCharsetConstant.UTF8MB4)
public class Attachment extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = 5026290395793755932L;

    /*** 文件名 */
    @TableField("name")
    @Column(name = "name", type = VARCHAR, length = 128, isNull = false, comment = "文件名")
    private String name;

    /*** 附件地址 */
    @TableField("attachment_address")
    @Column(name = "attachment_address", type = VARCHAR, isNull = false, comment = "附件地址")
    private String attachmentAddress;

    /*** 附件类型 */
    @TableField("attachment_type")
    @Column(name = "attachment_type", type = TINYINT, length = 4, comment = "附件类型")
    private Integer attachmentType;

    /*** 附件大小 */
    @TableField("attachment_size")
    @Column(name = "attachment_size", type = BIGINT, length = 20, isNull = false, comment = "附件大小")
    private Long attachmentSize;
}
