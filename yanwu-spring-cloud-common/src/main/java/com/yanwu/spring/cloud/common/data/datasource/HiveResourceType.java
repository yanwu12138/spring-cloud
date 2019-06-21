package com.yanwu.spring.cloud.common.data.datasource;

import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

public enum HiveResourceType {

    SYSTEM_DB,
    CONFIG_DB,
    REPORT_DB,
    CLIENT_DB,
    ACCOUNT_DB,
    CACHE_DB,
    IN_MEMORY_DB,
    GLOBAL_SYSMGR_DB,
    REGIONAL_SYSMGR_DB,
    REDIRECTOR_DB;

    /**
     * The sharding resources except CONFIG_DB.
     * All this resource types use the different sharding strategy from CONFIG_DB.
     */
    public static final HiveResourceType[] SHARDING_RESOURCES_EXCEPT_CONFIG_DB = {REPORT_DB, CLIENT_DB};

    /**
     * Returns a random enum constant
     *
     * @return a random enum constant
     */
    public static final HiveResourceType random() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }

    /**
     * Resolve HiveResourceType from the specific class via ResourceRegistrar annotation.
     *
     * @param clazz the class to resolve
     * @return annotated ResourceRegistrar value or null if not annotated
     */
    public static final HiveResourceType resolve(final Class<?> clazz) {
        checkNotNull(clazz);
        ResourceRegistrar rr = clazz.getAnnotation(ResourceRegistrar.class);
        return rr == null ? null : rr.type();
    }

    /**
     * Get HiveResourceType from the specific class via ResourceRegistrar annotation, the class MUST annotated this annotation,
     * otherwise an IllegalStateException will be thrown.
     *
     * @param clazz the class to resolve
     * @return annotated ResourceRegistrar value, cannot be null
     * @throws IllegalStateException if no ResourceRegistrar annotation
     */
    public static final HiveResourceType from(final Class<?> clazz) {
        HiveResourceType resourceType = resolve(clazz);
        if (resourceType == null) {
            throw new IllegalStateException(clazz + " MUST add @ResourceRegistrar annotation");
        }
        return resourceType;
    }

}
