package com.yanwu.spring.cloud.common.pojo;

import dk.tbsalling.aismessages.ais.messages.types.TransponderClass;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Baofeng Xu
 * @date 2021/11/30 9:53.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class AisDataBO implements Serializable {
    private static final long serialVersionUID = -2631795917035581726L;

    private Integer mmsi;

    /*** 发送设备类型
     * @see dk.tbsalling.aismessages.ais.messages.types.TransponderClass
     ***/
    private TransponderClass sclass;

    /*** 是否本船 ***/
    private Boolean ownship;

    /*** 接收时间 ***/
    private Long ctime;
}
