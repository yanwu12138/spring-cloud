package com.yanwu.spring.cloud.common.data.datasource;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Please do not explicitly register your entity class over here, and
 * use annotation on entity class for registration instead like
 *
 * @ResourceRegistrar(type=HiveResourceType.SYSTEM_DB) This is the registry for all entity objects for hosting database type
 */
@Component("hiveResourceTypeRegistry")
public class HiveResourceTypeRegistry {

    private final ConcurrentMap<Class<?>, HiveResourceType> registry = new ConcurrentHashMap<>();
    private final Map<HiveResourceType, List<Class<?>>> typeRegistry = new HashMap<>();

    private HiveResourceTypeRegistry() {
    }

    public HiveResourceType lookupResourceType(final Class<?> hiveObjectClass) {
        return registry.get(hiveObjectClass);
    }

    public synchronized void registerHiveObject(final Class<?> clazz, final HiveResourceType hiveResourceType) {
        registry.put(clazz, hiveResourceType);
        List<Class<?>> list = typeRegistry.get(hiveResourceType);
        if (list == null) {
            list = new ArrayList<>();
            typeRegistry.put(hiveResourceType, list);
        }
        list.add(clazz);
    }

    public synchronized Collection<Class<?>> getTypeList(final HiveResourceType hiveResourceType) {
        return Collections.unmodifiableCollection(typeRegistry.get(hiveResourceType));
    }

}
