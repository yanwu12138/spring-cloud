package com.yanwu.spring.cloud.postapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author XuBaofeng.
 * @date 2024/3/19 11:05.
 * <p>
 * description:
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.yanwu.spring.cloud"})
public class PostApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostApiApplication.class, args);
    }

}
