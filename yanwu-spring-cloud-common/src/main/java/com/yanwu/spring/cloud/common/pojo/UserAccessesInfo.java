package com.yanwu.spring.cloud.common.pojo;

import com.yanwu.spring.cloud.common.core.annotation.RequestHandler;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author XuBaofeng.
 * @date 2024/1/17 18:34.
 * <p>
 * description:
 */
@Data
@Accessors(chain = true)
public class UserAccessesInfo implements Serializable {
    private static final long serialVersionUID = 5582155659820461184L;

    private boolean userScope;
    private Set<Long> userIds;
    private boolean roleScope;
    private Set<Long> roleIds;

    public static UserAccessesInfo newInstance(RequestHandler annotation) {
        UserAccessesInfo instance = new UserAccessesInfo();
        instance.setUserScope(annotation.dataScope().shop());
        instance.setRoleScope(annotation.dataScope().agent());
        return instance.setUserIds(new HashSet<>()).setRoleIds(new HashSet<>());
    }

}