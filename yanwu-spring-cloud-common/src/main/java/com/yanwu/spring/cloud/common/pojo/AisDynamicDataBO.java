package com.yanwu.spring.cloud.common.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.tbsalling.aismessages.ais.messages.types.NavigationStatus;
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
public class AisDynamicDataBO extends AisDataBO implements Serializable {
    private static final long serialVersionUID = 4210420203760802229L;

    /*** 航行状态
     * @see dk.tbsalling.aismessages.ais.messages.types.NavigationStatus ***/
    private NavigationStatus navstatus;

    /*** 经度 ***/
    private BigDecimal lon;

    /*** 纬度 ***/
    private BigDecimal lat;

    /*** 艏向 ***/
    private BigDecimal heading;

    /*** 航向 ***/
    @JsonProperty("Course")
    private BigDecimal course;

    /*** 速度 ***/
    @JsonProperty("Sog")
    private BigDecimal sog;

    /*** 速度 ***/
    private Integer rturn;
}
