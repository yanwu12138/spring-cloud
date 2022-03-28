package com.yanwu.spring.cloud.common.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Baofeng Xu
 * @date 2022/3/25 18:19.
 * <p>
 * description:
 */
@SuppressWarnings("unused")
public class ObjectUtil {

    public ObjectUtil() {
        throw new UnsupportedOperationException("ObjectUtil should never be instantiated");
    }

    /**
     * 获取对象所有的属性(共有+私有)
     *
     * @param obj 对象
     * @param <T> T
     * @return 所有的属性
     */
    public static <T> Field[] fields(Object obj) {
        return fields(obj.getClass());
    }

    /**
     * 获取对象所有的属性(共有+私有)
     *
     * @param clazz 对象类型
     * @param <T>   T
     * @return 所有的属性
     */
    public static <T> Field[] fields(Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            return new Field[]{};
        }
        Field[] declaredFields = clazz.getDeclaredFields();
        if (declaredFields.length <= 0) {
            return clazz.getSuperclass().getDeclaredFields();
        }
        Field[] superDeclaredFields = clazz.getSuperclass().getDeclaredFields();
        if (superDeclaredFields.length <= 0) {
            return declaredFields;
        }
        return ArrayUtils.addAll(declaredFields, superDeclaredFields);
    }


    /**
     * 获取对象所有的属性的字段名(共有+私有)
     *
     * @param obj 对象
     * @param <T> T
     * @return 所有的属性
     */
    public static <T> String[] fieldNames(Object obj) {
        return fieldNames(fields(obj.getClass()));
    }

    /**
     * 获取对象所有的属性的字段名(共有+私有)
     *
     * @param clazz 对象类型
     * @param <T>   T
     * @return 所有的属性
     */
    public static <T> String[] fieldNames(Class<T> clazz) {
        return fieldNames(fields(clazz));
    }

    private static String[] fieldNames(Field[] fields) {
        if (fields == null || fields.length <= 0) {
            return new String[]{};
        }
        List<String> list = new ArrayList<>();
        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            list.add(field.getName());
        }
        if (CollectionUtils.isEmpty(list)) {
            return new String[]{};
        }
        String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

}
