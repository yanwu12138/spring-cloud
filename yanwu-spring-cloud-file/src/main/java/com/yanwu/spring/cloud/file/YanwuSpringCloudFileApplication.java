package com.yanwu.spring.cloud.file;

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

/**
 * @author XuBaofeng.
 * @date 2018-11-12 16:36.
 * <p>
 * description:
 * * @EnableEurekaClient        启动eureka注册
 * * @EnableDiscoveryClient     启动ribbon负载均衡
 * * @EnableFeignClients        启动feign服务发现功能
 * * @EnableHystrix             启动断路器
 */
@EnableHystrix
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.yanwu.spring.cloud.file.data.mapper")
@ComponentScan(basePackages = {"com.yanwu.spring.cloud"})
public class YanwuSpringCloudFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(YanwuSpringCloudFileApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
