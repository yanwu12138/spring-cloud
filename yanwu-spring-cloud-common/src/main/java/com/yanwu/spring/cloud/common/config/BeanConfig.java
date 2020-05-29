package com.yanwu.spring.cloud.common.config;

import com.yanwu.spring.cloud.common.utils.ObjectUtil;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/3 15:37.
 * <p>
 * description:
 */
@Configuration
public class BeanConfig {


    @Bean
    public Mapper getMapper() {
        return new DozerBeanMapper();
    }

    @Bean
    public ObjectUtil getObjectUtil() {
        return new ObjectUtil();
    }
}
