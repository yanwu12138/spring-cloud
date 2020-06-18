package com.yanwu.spring.cloud.common.demo.d02container.c01collection.c02set;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Set;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 17:28.
 * <p>
 * description:
 */
@Slf4j
class D00SetUtil {

    private static final Integer DEFAULT_SIZE = 10;
    private static final Integer DEFAULT_ITEM = 10;

    private static void add(Set<Integer> set) {
        add(set, DEFAULT_SIZE);
    }

    private static void add(Set<Integer> set, Integer size) {
        for (int i = 0; i < size; i++) {
            set.add(i);
        }
    }

    protected static void test(Set<Integer> set) {
        log.info("---------- {} ----------", set.getClass());
        add(set);
        set.add(DEFAULT_ITEM);
        log.info("list: {}", set);
        set.remove(5);
        log.info("list: {}", set);

        Iterator<Integer> iterator = set.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            log.info("next: {}", next);
            iterator.remove();
        }
    }
}
