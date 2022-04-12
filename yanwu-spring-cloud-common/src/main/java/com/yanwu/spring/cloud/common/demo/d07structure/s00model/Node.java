package com.yanwu.spring.cloud.common.demo.d07structure.s00model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-06 21:15:11.
 * <p>
 * describe: 单向节点
 */
@Data
public class Node<E extends Serializable> implements Serializable {
    private static final long serialVersionUID = -7916391747385636915L;

    private E value;
    private Node<E> last;
    private Node<E> next;

    public Node(E value) {
        this.value = value;
    }

}
