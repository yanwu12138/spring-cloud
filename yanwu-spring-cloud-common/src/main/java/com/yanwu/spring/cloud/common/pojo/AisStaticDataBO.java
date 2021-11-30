package com.yanwu.spring.cloud.common.pojo;

import dk.tbsalling.aismessages.ais.messages.types.ShipType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Baofeng Xu
 * @date 2021/11/30 9:54.
 * <p>
 * description:
 */
@Data
@ToString(callSuper = true)
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AisStaticDataBO extends AisDataBO implements Serializable {
    private static final long serialVersionUID = -7122973875631876786L;

    /*** IMO ***/
    private Integer imo;

    /*** 呼号 ***/
    private String callcode;

    /*** 船名 ***/
    private String name;

    /*** 目的地 ***/
    private String destination;

    /*** 船舶类型
     * @see dk.tbsalling.aismessages.ais.messages.types.ShipType
     * ***/
    private ShipType stype;

    /*** 距船首距离 ***/
    private Integer hrng;

    /*** 距船尾距离 ***/
    private Integer trng;

    /*** 距左舷距离 ***/
    private Integer lrng;

    /*** 距右舷距离 ***/
    private Integer rrng;

    /*** 吃水深度 ***/
    private BigDecimal draught;

    /*** 预计到达时间 ***/
    private Long atime;

}
