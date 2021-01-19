package com.yanwu.spring.cloud.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Baofeng Xu
 * @date 2021/1/19 14:41.
 * <p>
 * description: 输出项目的配置属性
 */

@Slf4j
@Component
public class ProjectConfigs implements CommandLineRunner {
    @Resource
    private Environment environment;

    @Override
    public void run(String... args) {
        log.info("-------------------- log project configs begin --------------------");
        try {
            for (PropertySource<?> sources : ((AbstractEnvironment) environment).getPropertySources()) {
                if (sources.getSource() instanceof Map) {
                    for (Map.Entry<?, ?> entry : ((Map<?, ?>) sources.getSource()).entrySet()) {
                        log.info("config property key: {} value: {}", logKey(entry.getKey()), entry.getValue());
                    }
                }
            }
        } catch (Exception e) {
            log.error("log project configs error.", e);
        }
        log.info("-------------------- log project configs end --------------------");
    }

    /**
     * 格式化输出KEY, 使日志对齐
     *
     * @param key KEY
     * @return 加长后的KEY
     */
    private String logKey(Object key) {
        int maxKeyLen = 64;
        StringBuilder builder = new StringBuilder(String.valueOf(key));
        while (builder.length() < maxKeyLen) {
            builder.append(" ");
        }
        return builder.toString();
    }

}
