package com.yanwu.spring.cloud.device.device;

import com.yanwu.spring.cloud.common.utils.VoDoUtil;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@EnableHystrix
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.yanwu.spring.cloud.device", "com.yanwu.spring.cloud.common"})
public class YanwuSpringCloudDeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(YanwuSpringCloudDeviceApplication.class, args);
    }

    @Bean
    public Mapper getMapper() {
        return new DozerBeanMapper();
    }

    @Bean
    public VoDoUtil getVoDoUtil() {
        return new VoDoUtil();
    }

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
