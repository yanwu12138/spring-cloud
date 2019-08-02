package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DataUtil {

    @SuppressWarnings("unchecked")
    static public <T> T unboxProxy(T candidate) {
        if (isProxy(candidate)) {
            try {
                return (T) ((HibernateProxy) candidate).getHibernateLazyInitializer().getImplementation();
            } catch (EntityNotFoundException e) {
                return null;
            }
        }
        return candidate;
    }

    static public boolean isProxy(Object candidate) {
        return candidate != null && candidate instanceof HibernateProxy;
    }

    public static <T> T initializeAndUnproxy(T entity) throws IllegalArgumentException, IllegalAccessException {
        if (entity == null) {
            throw new NullPointerException("Entity passed for initialization or unproxy is null");
        }

        Hibernate.initialize(entity);
        T result = unboxProxy(entity);

        initializeRecursively(result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void initializeRecursively(Object entity) throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = entity.getClass();
        if (!BaseObject.class.isAssignableFrom(clazz)) {
            return;
        }

        for (Field field : ReflectionUtil.getAllFields(clazz)) {
            Class<?> fieldClazz = field.getType();
            // only BaseObject field or Collection field might need unproxy
            if (!(BaseObject.class.isAssignableFrom(fieldClazz) || Collection.class.isAssignableFrom(fieldClazz))) {
                continue;
            }

            field.setAccessible(true);
            Object value = field.get(entity);
            Hibernate.initialize(value);

            if (value instanceof HibernateProxy) {
                value = ((HibernateProxy) value).getHibernateLazyInitializer().getImplementation();
                field.set(entity, value);
                initializeRecursively(value);
            } else if (value instanceof LazyInitializer) {
                value = ((LazyInitializer) value).getImplementation();
                initializeRecursively(value);
            } else if (value instanceof BaseObject) {
                initializeRecursively(value);
            } else if (value instanceof PersistentCollection) {
                PersistentCollection persistentCollection = (PersistentCollection) value;
                boolean dirty = persistentCollection.isDirty();
                Collection<Object> collection = (Collection<Object>) value;
                if (!collection.isEmpty()) {
                    Hibernate.initialize(collection);
                    List<Object> list = new LinkedList<>();
                    list.addAll(collection);
                    collection.clear();
                    for (Object object : list) {
                        object = unboxProxy(object);
                        initializeRecursively(object);
                        collection.add(object);
                    }
                    if (!dirty) {
                        persistentCollection.clearDirty();
                    }
                }
            }
        }
    }

    /**
     * 根据时间格式字符串获取时间戳
     *
     * @param time
     * @param type
     * @return
     * @throws Exception
     */
    public static Long getTimeLong(String time, String type) throws Exception {
        Date parse = new SimpleDateFormat(type).parse(time);
        return parse.getTime();
    }

    /**
     * 根据时间戳和格式获取时间字符串
     *
     * @param time
     * @param type
     * @return
     * @throws Exception
     */
    public static String getTimeString(Long time, String type) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        return sdf.format(new Date(time));
    }

    private DataUtil() {
    }
}