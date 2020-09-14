package com.yanwu.spring.cloud.device.data.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.yanwu.spring.cloud.common.pojo.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2020/9/14 17:20.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("ip_address")
public class IpAddress extends BaseDo<Long> implements Serializable {

    /*** IP */
    @TableField("ip_str")
    private String ipStr;

    /*** ipInt */
    @TableField("ip_int")
    private Integer ipInt;

    /*** ipLong */
    @TableField("ip_long")
    private Long ipLong;

}
