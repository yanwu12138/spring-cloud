package com.yanwu.spring.cloud.common.demo.d07structure.s02stack;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-07 21:08:36.
 * <p>
 * describe:
 */
public interface S00Stack<E extends Serializable> {

    void push(E value);

    E pop();

    E top();

    int size();
}
