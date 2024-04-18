package com.yanwu.spring.cloud.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.yanwu.spring.cloud.common.core.common.Contents;
import com.yanwu.spring.cloud.common.pojo.AccessToken;
import com.yanwu.spring.cloud.common.utils.ContextUtil;
import com.yanwu.spring.cloud.common.utils.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020-03-26 18:39.
 * <p>
 * description:
 */
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    /**
     * 插入时自动插入字段
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime timestamp = LocalDateTime.now();
        // ===== 创建时间
        Object created = getFieldValByName("created", metaObject);
        if (Objects.isNull(created)) {
            setFieldValByName("created", timestamp, metaObject);
        }
        // ===== 更改时间
        Object updated = getFieldValByName("updated", metaObject);
        if (Objects.isNull(updated)) {
            setFieldValByName("updated", timestamp, metaObject);
        }
        // ===== 数据有效性
        Object enabled = getFieldValByName("enabled", metaObject);
        if (Objects.isNull(enabled)) {
            setFieldValByName("enabled", true, metaObject);
        }
        handlerAccount(metaObject, "creator");
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object updated = getFieldValByName("updated", metaObject);
        if (updated == null) {
            setFieldValByName("updated", new Timestamp(System.currentTimeMillis()), metaObject);
        }
        handlerAccount(metaObject, "updator");
    }

    private void handlerAccount(MetaObject metaObject, String field) {
        String token = ContextUtil.header(Contents.TOKEN);
        if (StringUtils.isBlank(token)) {
            AccessToken accessToken = TokenUtil.verifyToken(token);
            if (accessToken.getId() != null) {
                Object enabled = getFieldValByName(field, metaObject);
                if (Objects.isNull(enabled)) {
                    setFieldValByName(field, accessToken.getId(), metaObject);
                }
            }
        }
    }

}
