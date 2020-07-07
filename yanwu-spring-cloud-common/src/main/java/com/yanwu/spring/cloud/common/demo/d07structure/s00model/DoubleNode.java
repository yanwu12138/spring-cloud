package com.yanwu.spring.cloud.common.demo.d07structure.s00model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-06 21:23:07.
 * <p>
 * describe: 双向节点
 */
@Data
public class DoubleNode<E extends Serializable> implements Serializable {
    private static final long serialVersionUID = 275042274537083774L;

    private E value;
    private DoubleNode<E> last;
    private DoubleNode<E> next;

    public DoubleNode(E value) {
        this.value = value;
    }
}
