package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.dozer.loader.api.TypeMappingOptions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
public class VoDoUtil implements ApplicationContextAware, InitializingBean {

    private ApplicationContext ctx;

    @Resource
    private Mapper dozer;

    public <VO> VO convertDoToVo(Object doObject, Class<VO> voClass) throws Exception {
        return doObject == null ? null : this.map(doObject, voClass);
    }

    public <DO> DO convertVoToDo(Object voObject, Class<DO> doClass) throws Exception {
        return voObject == null ? null : this.map(voObject, doClass);
    }

    public <T> T map(Object source, Class<T> destinationClass) {
        return source == null ? null : dozer.map(source, destinationClass);
    }

    /**
     * 属性copy，当源sources属性为null或者空串时，不拷贝
     *
     * @param sources
     * @param destination
     */
    public void copyProperties(final Object sources, final Object destination) {
        WeakReference<DozerBeanMapper> weakReference = new WeakReference<DozerBeanMapper>(new DozerBeanMapper());
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

    public <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return null;
        }
        List<T> destinationList = new ArrayList<T>();
        for (Object sourceObject : sourceList) {
            T destinationObject = dozer.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    /**
     * Copy all properties of source Object to target Object
     *
     * @param source            source object
     * @param destinationObject target object
     */
    public void copy(Object source, Object destinationObject) {
        dozer.map(source, destinationObject);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        dozer = ctx.getBean(Mapper.class);
        dozer = new DozerBeanMapper();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
        log.info("setApplicationContext: {}", applicationContext);
    }

}