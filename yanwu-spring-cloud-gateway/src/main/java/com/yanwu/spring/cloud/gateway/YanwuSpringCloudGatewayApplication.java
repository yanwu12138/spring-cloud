package com.yanwu.spring.cloud.gateway;

import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Administrator
 */
@EnableHystrix
@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.yanwu.spring.cloud.gateway", "com.yanwu.spring.cloud.common"})
public class YanwuSpringCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(YanwuSpringCloudGatewayApplication.class, args);
    }

    @Bean
    public Mapper getMapper() {
        return new DozerBeanMapper();
    }

    @Bean
    public VoDoUtil getVoDoUtil() {
        return new VoDoUtil();
    }
}
