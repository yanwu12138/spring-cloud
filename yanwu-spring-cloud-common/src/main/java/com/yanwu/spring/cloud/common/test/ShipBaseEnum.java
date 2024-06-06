package com.yanwu.spring.cloud.common.test;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/6/5 21:18.
 * <p>
 * description:
 */
@SuppressWarnings("all")
public interface ShipBaseEnum {

    int getCode();

    String getDesc();

    /***
     * 根据指定的枚举类和code获取枚举项
     * @param clazz 枚举类
     * @param code  枚举code
     * @return code对应的枚举项
     */
    public static <E extends ShipBaseEnum> E getByCode(Class<E> clazz, int code) {
        if (clazz == null) {
            return null;
        }
        E[] enumConstants = clazz.getEnumConstants();
        if (enumConstants == null || enumConstants.length == 0) {
            return null;
        }
        for (E item : enumConstants) {
            if (item != null && code == item.getCode()) {
                return item;
            }
        }
        return null;
    }

    /***
     * 将指定的枚举类的所有枚举项返回给前端
     * @param clazz 枚举类
     * @return 所有枚举项
     */
    public static <E extends ShipBaseEnum> List<EnumItem> shipEnumItems(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        E[] enumConstants = clazz.getEnumConstants();
        if (enumConstants == null || enumConstants.length == 0) {
            return Collections.emptyList();
        }
        List<EnumItem> enumValues = new ArrayList<>();
        for (E item : enumConstants) {
            enumValues.add(EnumItem.newInstance(item.getCode(), item.getDesc()));
        }
        return enumValues;
    }


    /*** 船旗国 ***/
    @Getter
    public enum FlagStateEnum implements ShipBaseEnum {
        CHINA(0, "中国"),
        CHN(1, "CHN"),
        KIRIBATI(2, "基里巴斯"),
        ARGENTINA(3, "阿根廷"),
        ;

        private final int code;
        private final String desc;

        FlagStateEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /*** 船舶类型 ***/
    @Getter
    public enum ShipTypeEnum implements ShipBaseEnum {
        FISHING_VESSEL(0, "渔船"),
        TRANSPORT_VESSEL(1, "运输船"),
        ;

        private final int code;
        private final String desc;

        ShipTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /*** 作业类型 ***/
    @Getter
    public enum WorkTypeEnum implements ShipBaseEnum {
        OTHER(0, "其他"),
        FISHING_TACKLE(1, "钓具"),
        SINGLE_TRAWL(2, "单拖网"),
        TRAWL_FIXED_NETS(3, "拖网定置网"),
        SQUID_FISHING(4, "鱿钓"),
        LOW_TEMP_TUNA_FISHING(5, "超低温金枪鱼延绳钓"),
        BOTTOM_LONGLINE(6, "底层延绳钓"),
        LIGHT_PURSE_SEINE(7, "灯光围网"),
        BROADSIDE_NETS(8, "舷提网"),
        BOTTOM_FISHING(9, "底层钓"),
        FENCE(10, "围网"),
        TUNA_FENCE(11, "金枪鱼围网"),
        TUNA_LONGLINE(12, "金枪鱼延绳钓"),
        KITE_THIEF_FISHING(13, "鸢鸟贼钓"),
        LIGHTING_NETTING(14, "灯光敷网"),
        CANTILEVER_BRACKET_TRAWL_NETS(15, "悬臂支架拖虾网"),
        ;

        private final int code;
        private final String desc;

        WorkTypeEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /*** 作业区域 ***/
    @Getter
    public enum WorkAreaEnum implements ShipBaseEnum {
        INDIAN_OCEAN(0, "印度洋"),
        NORTH_PACIFIC(1, "北太平洋"),
        PACIFIC_OCEAN(2, "太平洋"),
        SOUTHWEST_ATLANTIC(3, "西南大西洋"),
        ANTARCTIC_WATERS(4, "南极海域"),
        WESTERN_PACIFIC_AND_EASTERN_PACIFIC(5, "西太平洋东太平洋"),
        SOUTH_PACIFIC(6, "南太平洋"),
        ATLANTIC_OCEAN(7, "大西洋"),
        SOUTHEAST_PACIFIC(8, "东南太平洋"),
        MIDWESTERN_PACIFIC(9, "中西部太平洋"),
        FIJIAN_WATERS(10, "斐济海域"),
        MOZAMBIQUE(11, "莫桑比克专属经济区"),
        MALAYSIA(12, "马来西亚专属经济区"),
        IRAN(13, "伊朗专属经济区"),
        ;

        private final int code;
        private final String desc;

        WorkAreaEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    /*** 国籍 ***/
    @Getter
    public enum NationalityEnum implements ShipBaseEnum {
        CHINA(0, "中国"),
        USA(1, "美国"),
        RUSSIA(2, "俄罗斯"),
        ;

        private final int code;
        private final String desc;

        NationalityEnum(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    @Data
    @Accessors(chain = true)
    public static class EnumItem implements Serializable {
        private int code;
        private String desc;

        public static EnumItem newInstance(int code, String desc) {
            return new EnumItem().setCode(code).setDesc(desc);
        }
    }

}