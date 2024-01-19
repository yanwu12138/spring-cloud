package com.yanwu.spring.cloud.common.core.enums;

import com.yanwu.spring.cloud.common.pojo.UserAccessesInfo;
import lombok.Getter;

import java.util.Collections;
import java.util.Set;

/**
 * @author XuBaofeng.
 * @date 2024/1/17 18:40.
 * <p>
 * description:
 */
@Getter
public enum AccessTypeEnum {

    USER(1),
    ROLE(2),
    ;

    private final int type;

    AccessTypeEnum(int type) {
        this.type = type;
    }

    public static AccessTypeEnum getInstance(int accesses) {
        for (AccessTypeEnum value : AccessTypeEnum.values()) {
            if (value.type == accesses) {
                return value;
            }
        }
        return null;
    }

    /***
     * 数据过滤类型【一级代理商 || 店铺】来获取对应的属性值
     * @param userAccesses 用户的数据权限集合（登录时刷新入缓存）
     * @return 数据权限集合
     */
    public Set<Long> accessIds(UserAccessesInfo userAccesses) {
        switch (this) {
            case USER:
                return userAccesses.getUserIds();
            case ROLE:
                return userAccesses.getRoleIds();
            default:
                return Collections.emptySet();
        }
    }

}
