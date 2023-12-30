package com.yanwu.spring.cloud.common.core.enums;

import com.yanwu.spring.cloud.common.utils.GeoUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author XuBaofeng.
 * @date 2023/12/29 17:13.
 * <p>
 * description:
 */
@Getter
public enum PositionEnum {

    EAST("E", "东"),
    WEST("W", "西"),
    SOUTH("S", "南"),
    NORTH("N", "北"),
    ;

    private final String code;
    private final String desc;

    PositionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PositionEnum getInstance(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (PositionEnum value : PositionEnum.values()) {
            if (code.equalsIgnoreCase(value.code)) {
                return value;
            }
        }
        return null;
    }

    public static PositionEnum getInstance(double position, boolean flag) {
        return getInstance(BigDecimal.valueOf(position), flag);
    }

    public static PositionEnum getInstance(BigDecimal position, boolean flag) {
        if (flag) {
            // ----- 经度
            if (position == null || position.compareTo(GeoUtil.MIN_LNG) < 0 || position.compareTo(GeoUtil.MAX_LNG) > 0) {
                return null;
            }
            return position.compareTo(BigDecimal.ZERO) >= 0 ? EAST : WEST;
        } else {
            // ----- 纬度
            if (position == null || position.compareTo(GeoUtil.MIN_LAT) < 0 || position.compareTo(GeoUtil.MAX_LAT) > 0) {
                return null;
            }
            return position.compareTo(BigDecimal.ZERO) >= 0 ? NORTH : SOUTH;
        }
    }

}
