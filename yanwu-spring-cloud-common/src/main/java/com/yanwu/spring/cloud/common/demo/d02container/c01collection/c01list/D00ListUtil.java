package com.yanwu.spring.cloud.common.demo.d02container.c01collection.c01list;

import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/6/18 15:25.
 * <p>
 * description:
 */
@Slf4j
public class D00ListUtil {
    private static final Integer DEFAULT_SIZE = 10;
    private static final Integer DEFAULT_ITEM = 10;

    private static void add(List<Integer> list) {
        add(list, DEFAULT_SIZE);
    }

    private static void add(List<Integer> list, Integer size) {
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
    }

    protected static void test(List<Integer> list) {
        log.info("---------- {} ----------", list.getClass());
        add(list);
        list.add(DEFAULT_ITEM);
        log.info("list: {}", list);
        list.remove(5);
        log.info("list: {}", list);
        Integer get = list.get(5);
        log.info("get: {}", get);

        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            log.info("next: {}", next);
            iterator.remove();
        }
    }
}
