package com.yanwu.spring.cloud.common.demo.d07structure.s01list;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-07 23:12:21.
 * <p>
 * describe:
 */
@Slf4j
public class S01ArrayList<E extends Serializable> implements S00List<E> {
    private final E[] values;
    private int top;
    private final int size;

    @SuppressWarnings("all")
    public S01ArrayList(int size) {
        this.top = -1;
        this.size = size;
        this.values = (E[]) new Object[size];
    }

    @Override
    public boolean add(E value) {
        if (value == null || size() >= size) {
            return false;
        }
        values[++top] = value;
        return true;
    }

    @Override
    public E get(int index) {
        if (size() == 0 || index < 0 || index >= size()) {
            return null;
        }
        return values[index];
    }

    @Override
    public E set(int index, E newVal) {
        if (index < 0 || index >= size() || newVal == null) {
            return null;
        }
        E result = values[index];
        values[index] = newVal;
        return result;
    }

    @Override
    public int set(E oldVal, E newVal) {
        int result = 0;
        if (oldVal == null || newVal == null || size() == 0) {
            return result;
        }
        for (int index = 0; index < values.length; index++) {
            if (Objects.equals(values[index], oldVal)) {
                values[index] = newVal;
                result++;
            }
        }
        return result;
    }

    @Override
    public int del(E value) {
        int delNum = set(value, null);
        top = top - delNum;
        return delNum;
    }

    @Override
    public E del(int index) {
        if (size() == 0 || index < 0 || index >= size()) {
            return null;
        }
        E result = set(index, null);
        if (result != null) {
            top--;
        }
        return result;
    }

    @Override
    public void reverse() {

    }

    @Override
    public int size() {
        return top + 1;
    }


}
