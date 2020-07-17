package com.yanwu.spring.cloud.common.demo.d07structure.s01list;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-07 23:12:21.
 * <p>
 * describe:
 */
@Slf4j
public class S01ArrayList<E extends Serializable> implements S00List<Integer> {
    private final Integer[] values;
    private int top;
    private final int size;

    public S01ArrayList(int size) {
        this.top = -1;
        this.size = size;
        this.values = new Integer[size];
    }

    @Override
    public Integer insert(Integer value) {
        if (value == null || size == size()) {
            return null;
        }
        values[++top] = value;
        return value;
    }

    @Override
    public Integer select(Integer index) {
        if (index < 0 || index > size() || size() == 0) {
            return null;
        }
        return values[index];
    }

    @Override
    public Integer update(Integer index, Integer newVal) {
        if (index < 0 || index > size() || size() == 0) {
            return null;
        }
        Integer value = values[index];
        values[index] = newVal;
        return value;
    }

    @Override
    public Integer delete(Integer index) {
        if (index < 0 || index > size() || size() == 0) {
            return null;
        }
        Integer value = values[index];
        int i = index;
        while (i < size()) {
            values[i] = values[++i];
            values[top--] = null;
        }
        return value;
    }

    public int size() {
        return top + 1;
    }

    public static void main(String[] args) {
        S01ArrayList<Integer> list = new S01ArrayList<>(5);
        log.info("list: [insert] {}", list.insert(1));
        log.info("list: [insert] {}", list.insert(1));
        log.info("list: [insert] {}", list.insert(1));
        log.info("list: [insert] {}", list.insert(4));
        log.info("list: [insert] {}", list.insert(1));
        log.info("list: [insert] {}", list.insert(5));

        log.info("list: [delete] {}", list.delete(3));

        log.info("list: [update] {}", list.update(1, 9));

        log.info("list: [select] {}", list.select(1));

        log.info("list: [size] {}", list.size());

        log.info("list: [insert] {}", list.insert(4));

        log.info("list: [size] {}", list.size());
    }
}
