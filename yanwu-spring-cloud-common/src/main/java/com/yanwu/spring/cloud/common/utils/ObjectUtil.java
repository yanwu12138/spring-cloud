package com.yanwu.spring.cloud.common.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Baofeng Xu
 * @date 2022/3/25 18:19.
 * <p>
 * description:
 */
@SuppressWarnings("unused")
@Slf4j
public class ObjectUtil {

    private ObjectUtil() {
        throw new UnsupportedOperationException("ObjectUtil should never be instantiated");
    }

    /***
     * 判断是否是静态函数
     * @param method 函数
     * @return 【true: 是静态; false: 非静态】
     */
    public static boolean isStatic(@NonNull Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    /***
     * 判断是否是静态字段
     * @param field 字段
     * @return 【true: 静态; false: 非静态】
     */
    public static boolean isStatic(@NonNull Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    /***
     * 根据对象和字段名获取字段对象
     * @param obj       对象
     * @param fieldName 字段名
     */
    public static Field field(Object obj, String fieldName) {
        if (obj == null || obj.getClass() == null || StringUtils.isBlank(fieldName)) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        Field result = null;
        if (declaredFields.length > 0 && (result = field(declaredFields, fieldName)) != null) {
            return result;
        }
        Set<Field> superFields = new HashSet<>();
        superclassField(clazz, superFields);
        if (!superFields.isEmpty()) {
            result = field(superFields, fieldName);
        }
        return result;
    }

    /***
     * 根据对象类型找到所有的属性
     * @param clazz  对象
     * @param result 结果集
     */
    private static void superclassField(Class<?> clazz, Set<Field> result) {
        if (clazz == null) {
            return;
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass == null) {
            return;
        }
        Field[] superclassFields = superclass.getDeclaredFields();
        if (superclassFields.length > 0) {
            result.addAll(Arrays.asList(superclassFields));
        }
        superclassField(superclass, result);
    }

    /***
     * 从属性集合中找到对应的属性
     * @param fields     属性集
     * @param failedName 属性名称
     */
    private static Field field(Collection<Field> fields, String failedName) {
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }
        for (Field field : fields) {
            if (failedName.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    /***
     * 从属性集合中找到对应的属性
     * @param fields     属性集
     * @param failedName 属性名称
     */
    private static Field field(Field[] fields, String failedName) {
        if (fields.length == 0) {
            return null;
        }
        for (Field field : fields) {
            if (failedName.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    /***
     * 获取对象所有的属性(共有+私有)
     * @param obj 对象
     * @param <T> T
     * @return 所有的属性
     */
    public static <T> Field[] fields(Object obj) {
        return fields(obj.getClass());
    }

    /***
     * 获取对象所有的属性(共有+私有)
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


    /***
     * 获取对象所有的属性的字段名(共有+私有)
     * @param obj 对象
     * @param <T> T
     * @return 所有的属性
     */
    public static <T> String[] fieldNames(Object obj) {
        return fieldNames(fields(obj.getClass()));
    }

    /***
     * 获取对象所有的属性的字段名(共有+私有)
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

    /***
     * 根据属性名从对象中获取对应的属性值
     * @param obj       对象
     * @param fieldName 属性名
     * @return 属性值
     */
    public static Object fieldValue(Object obj, String fieldName) throws Exception {
        if (obj == null || Objects.isNull(obj.getClass()) || StringUtils.isBlank(fieldName)) {
            return null;
        }
        Class<?> clazz = obj.getClass();
        Field field = field(obj, fieldName);
        if (field == null) {
            return null;
        }
        field.setAccessible(true);
        return field.get(obj);
    }

}
