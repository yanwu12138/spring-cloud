package com.yanwu.spring.cloud.common.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Baofeng Xu
 * @date 2021/9/17 20:01.
 * <p>
 * description:
 */
@Slf4j
@Component
public class InitSqlRunner implements CommandLineRunner {
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            if (jdbcTemplate == null) {
                // ----- 无数据库连接，不执行
                return;
            }
            org.springframework.core.io.Resource resource = new ClassPathResource("init.sql");
            if (!resource.exists()) {
                // ----- init.sql文件不存在，无初始化脚本，直接结束
                return;
            }
            try (InputStream inputStream = resource.getInputStream();
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String sql;
                while ((sql = bufferedReader.readLine()) != null) {
                    if (StringUtils.isBlank(sql) || sql.startsWith("--")) {
                        // ----- 为空行或者为注解，不执行
                        continue;
                    }
                    try {
                        jdbcTemplate.execute(sql);
                    } catch (Exception e) {
                        log.warn("init sql execute error. sql: {}", sql, e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("init sql error.", e);
            System.exit(-1);
        }
    }
}
