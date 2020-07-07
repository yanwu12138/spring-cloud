package com.yanwu.spring.cloud.common.demo.d07structure.s01list;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-07 23:00:38.
 * <p>
 * describe:
 */
public interface S00List<E extends Serializable> {

    E insert(E value);

    E select(E value);

    E update(E oldVal, E newVal);

    E delete(E value);
}
