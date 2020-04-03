package com.yanwu.spring.cloud.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
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
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
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
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object updated = getFieldValByName("updated", metaObject);
        if (updated == null) {
            setFieldValByName("updated", new Timestamp(System.currentTimeMillis()), metaObject);
        }
    }

}
