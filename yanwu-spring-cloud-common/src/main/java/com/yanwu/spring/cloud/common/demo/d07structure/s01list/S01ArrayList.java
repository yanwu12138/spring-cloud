package com.yanwu.spring.cloud.common.demo.d07structure.s01list;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-07 23:12:21.
 * <p>
 * describe:
 */
public class S01ArrayList<E extends Serializable> implements S00List<E> {
    @Override
    public E insert(E value) {
        return null;
    }

    @Override
    public E select(E value) {
        return null;
    }

    @Override
    public E update(E oldVal, E newVal) {
        return null;
    }

    @Override
    public E delete(E value) {
        return null;
    }
}
