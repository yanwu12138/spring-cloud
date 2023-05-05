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
import java.util.concurrent.Executor;

/**
 * @author Baofeng Xu
 * @date 2021/9/17 20:01.
 * <p>
 * description: 服务启动后执行init.sql中的SQL语句
 * 注意：当某个项目没有数据库支持时，需要排除该类的加载，不然会抛异常
 * 排除方式：@ComponentScan(basePackages = {"xxx.xxx"}, excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {InitSqlRunner.class})})
 */
@Slf4j
@Component
public class InitSqlRunner implements CommandLineRunner {
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private Executor initExecutors;

    @Override
    public void run(String... args) throws Exception {
        initExecutors.execute(() -> {
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
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String sql;
                    while (StringUtils.isNotBlank(sql = reader.readLine())) {
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
        });
    }
}
