package com.yanwu.spring.cloud.common.runner;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Baofeng Xu
 * @date 2021/1/19 14:41.
 * <p>
 * description: 输出项目的配置属性
 */

@Slf4j
@Component
public class LogProjectConfigs {
    @Resource
    private Environment environment;

    @PostConstruct
    public void logConfigs() {
        log.info("======================================== log project configs begin ========================================");
        try {
            SortedMap<String, Object> configs = new TreeMap<>();
            for (PropertySource<?> sources : ((AbstractEnvironment) environment).getPropertySources()) {
                if (!(sources.getSource() instanceof Map)) {
                    continue;
                }
                Map<?, ?> source = (Map<?, ?>) sources.getSource();
                source.forEach((key, value) -> configs.put(String.valueOf(key), value));
            }
            int maxLen = getMaxLen(configs.keySet());
            configs.forEach((key, value) -> log.info("config property key: {} value: {}", logKey(key, maxLen), value));
        } catch (Exception e) {
            log.error("log project configs error.", e);
        }
        log.info("======================================== log project configs end ========================================");
    }

    /***
     * 找到最长的KEY的长度
     * @param keySet KEY集合
     * @return 最长的长度
     */
    private int getMaxLen(Set<String> keySet) {
        if (CollectionUtils.isEmpty(keySet)) {
            return -1;
        }
        final int[] maxLen = {0};
        keySet.forEach(key -> maxLen[0] = Math.max(maxLen[0], key.length()));
        return maxLen[0];
    }

    /**
     * 格式化输出KEY, 使日志对齐
     *
     * @param key KEY
     * @return 加长后的KEY
     */
    private String logKey(String key, int maxLen) {
        maxLen = maxLen <= 0 ? 64 : maxLen;
        StringBuilder builder = new StringBuilder(key);
        while (builder.length() < maxLen) {
            builder.append(" ");
        }
        return builder.toString();
    }

}
