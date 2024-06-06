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

    /***
     * 根据十进制经纬度和类型获取方位枚举
     * @param position 十进制经纬度
     * @param flag     类型[true: 经度; false: 纬度]
     * @return 方位枚举数据
     */
    public static PositionEnum getInstance(double position, boolean flag) {
        return getInstance(BigDecimal.valueOf(position), flag);
    }

    /***
     * 根据十进制经纬度和类型获取方位枚举
     * @param position 十进制经纬度
     * @param flag     类型[true: 经度; false: 纬度]
     * @return 方位枚举数据
     */
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

    /***
     * 根据度分秒格式的经纬度计算十进制经纬度的符号
     * @param decimal  十进制经纬度
     * @param position 度分秒经纬度
     * @return 计算符号后的十进制经纬度
     */
    public static BigDecimal calcSymbols(BigDecimal decimal, String position) {
        if (position.endsWith(EAST.getCode()) || position.endsWith(NORTH.getCode())) {
            return decimal;
        } else {
            return BigDecimal.ZERO.subtract(decimal);
        }
    }

}
