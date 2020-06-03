package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * voDoUtil could copy properties of A to B Please use @Mapping to map different
 * field It also support to transfer List to List List to Array Array to Array
 * Set to Set Set to Array Set to List
 *
 * @author Administrator
 */
@Slf4j
@Component
public class ObjectUtil implements InitializingBean {

    @Resource
    private Mapper mapper;

    public <T> T convert(Object doObject, Class<T> clazz) {
        return doObject == null ? null : this.map(doObject, clazz);
    }

    public <T> T map(Object source, Class<T> clazz) {
        return source == null ? null : mapper.map(source, clazz);
    }

    public <T> List<T> mapList(Collection<?> sourceList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return null;
        }
        List<T> destinationList = new ArrayList<>();
        for (Object sourceObject : sourceList) {
            T destinationObject = mapper.map(sourceObject, clazz);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    public void copy(Object source, Object destinationObject) {
        mapper.map(source, destinationObject);
    }

    /**
     * 属性copy，当源sources属性为null或者空串时，不拷贝
     *
     * @param sources
     * @param destination
     */
    public void copyProperties(final Object sources, final Object destination) {
        WeakReference<DozerBeanMapper> weakReference = new WeakReference<>(new DozerBeanMapper());
        DozerBeanMapper mapper = weakReference.get();
        mapper.addMapping(new BeanMappingBuilder() {
            @Override
            protected void configure() {
                // mapping(sources.getClass(), destination.getClass(), TypeMappingOptions.mapNull(false), TypeMappingOptions.mapEmptyString(false))
                mapping(sources.getClass(), destination.getClass(), TypeMappingOptions.mapNull(false));
            }
        });
        mapper.map(sources, destination);
        mapper.destroy();
        weakReference.clear();
    }


    @Override
    public void afterPropertiesSet() {
        mapper = new DozerBeanMapper();
    }


}