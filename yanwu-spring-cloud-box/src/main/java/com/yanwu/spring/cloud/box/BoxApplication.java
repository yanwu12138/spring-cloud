package com.yanwu.spring.cloud.box;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan(basePackages = {"com.gitee.sunchenbin.mybatis.actable.dao.*"})
@ComponentScan(basePackages = {"com.yanwu.spring.cloud", "com.gitee.sunchenbin.mybatis.actable.manager.*"})
public class BoxApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoxApplication.class, args);
    }

}
