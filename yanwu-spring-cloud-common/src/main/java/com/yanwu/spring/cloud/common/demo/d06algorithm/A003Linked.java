package com.yanwu.spring.cloud.common.demo.d06algorithm;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Baofeng Xu
 * @date 2022/4/2 15:35.
 * <p>
 * description: 链表
 */
public class A003Linked {

    /**
     * 问题1：删除链表中指定的值
     */
    public static Node<Integer> code_1(Node<Integer> head, Integer del) {
        if (head == null) {
            return null;
        }
        while (head != null) {
            if (!Objects.equals(head.value, del)) {
                break;
            }
            head = head.last;
        }
        return null;
    }


    @Data
    @Accessors(chain = true)
    private static class Node<T extends Serializable> implements Serializable {
        private static final long serialVersionUID = -7277523549418003710L;
        /*** 前节点 ***/
        private Node<T> last;
        /*** 后节点 ***/
        private Node<T> next;
        /*** 数据 ***/
        private T value;
    }

}
