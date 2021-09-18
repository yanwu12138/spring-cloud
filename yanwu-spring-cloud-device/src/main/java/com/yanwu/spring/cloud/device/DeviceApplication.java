package com.yanwu.spring.cloud.device;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@EnableHystrix
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan({"com.yanwu.spring.cloud.device.data.mapper", "com.gitee.sunchenbin.mybatis.actable.dao.*"})
@ComponentScan(basePackages = {"com.yanwu.spring.cloud", "com.gitee.sunchenbin.mybatis.actable.manager.*"})
public class DeviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
