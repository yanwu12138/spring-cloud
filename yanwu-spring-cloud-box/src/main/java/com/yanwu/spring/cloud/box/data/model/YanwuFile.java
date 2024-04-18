package com.yanwu.spring.cloud.box.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.Index;
import com.gitee.sunchenbin.mybatis.actable.annotation.Table;
import com.gitee.sunchenbin.mybatis.actable.annotation.Unique;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlCharsetConstant;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

import static com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant.VARCHAR;

/**
 * @author XuBaofeng.
 * @date 2024/4/18 16:53.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("yanwu_file")
@Table(name = "yanwu_file", comment = "文件表", charset = MySqlCharsetConstant.UTF8MB4)
public class YanwuFile extends BaseDo<Long> implements Serializable {
    private static final long serialVersionUID = -5239672047400129547L;

    @TableField("year")
    @Column(name = "year", type = VARCHAR, length = 10, isNull = false, comment = "文件创建时间-年")
    private String year;

    @TableField("month")
    @Index(value = "year_month", columns = {"year", "month"})
    @Column(name = "month", type = VARCHAR, length = 10, isNull = false, comment = "文件创建时间-月")
    private String month;

    @TableField("path")
    @Unique(value = "path", columns = "path")
    @Column(name = "path", type = VARCHAR, isNull = false, comment = "文件所在绝对路径")
    private String path;

    @TableField("url")
    @Unique(value = "url", columns = "url")
    @Column(name = "url", type = VARCHAR, isNull = false, comment = "文件播放地址")
    private String url;

    @TableField("thumbnail")
    @Column(name = "thumbnail", type = VARCHAR, isNull = false, comment = "缩略图地址")
    private String thumbnail;

    @TableField("mark")
    @Index(value = "mark", columns = {"mark"})
    @Column(name = "mark", type = VARCHAR, length = 32, isNull = false, comment = "文件标签")
    private String mark;

    @TableField("type")
    @Column(name = "type", type = VARCHAR, length = 32, isNull = false, comment = "文件类型")
    private String type;

}
