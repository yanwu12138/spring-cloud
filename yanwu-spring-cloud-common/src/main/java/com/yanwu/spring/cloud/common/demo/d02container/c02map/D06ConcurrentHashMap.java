package com.yanwu.spring.cloud.common.demo.d02container.c02map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 13:49.
 * <p>
 * description:
 */
public class D06ConcurrentHashMap {

    public static void main(String[] args) {
        Map<String, String> map = new ConcurrentHashMap<>();
    }
}
