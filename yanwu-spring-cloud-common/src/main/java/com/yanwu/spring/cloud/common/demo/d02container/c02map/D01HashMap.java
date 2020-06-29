package com.yanwu.spring.cloud.common.demo.d02container.c02map;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 13:49.
 * <p>
 * description:
 */
@Slf4j
public class D01HashMap {

    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, D00MapUtil.str());
        map.put(2, D00MapUtil.str());
        map.put(3, D00MapUtil.str());
        map.put(4, D00MapUtil.str());
        map.put(5, D00MapUtil.str());
        log.info("hash map: {}", map);
        log.info("---------- map lambda ----------");
        map.forEach((key, value) -> {
            log.info("entries key: {}, value: {}", key, value);
        });
        log.info("---------- Entry lambda ----------");
        Set<Map.Entry<Integer, String>> entries = map.entrySet();
        entries.forEach(e -> {
            log.info("entries key: {}, value: {}", e.getKey(), e.getValue());
        });
    }

}
