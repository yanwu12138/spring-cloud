package com.yanwu.spring.cloud.common.demo.d07structure.s01list;

import com.yanwu.spring.cloud.common.demo.d07structure.s00model.DoubleNode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-06 21:27:24.
 * <p>
 * describe: 双链表
 */
@Slf4j
public class S03DoubleList<E extends Serializable> {
    /*** 链表的元素个数 ***/
    private Integer size;
    /*** 链表的头节点 ***/
    private DoubleNode<E> head;
    /*** 链表的尾节点 ***/
    private DoubleNode<E> tail;

    public S03DoubleList() {
        this.size = 0;
    }

    /**
     * 插入节点
     *
     * @param value 值
     * @return 链表
     */
    public DoubleNode<E> insert(E value) {
        if (value == null) {
            // ----- 值为null时不插入，直接返回当前链表
            return head;
        }
        // ----- 创建一个新节点
        DoubleNode<E> newNode = new DoubleNode<>(value);
        if (head == null) {
            // ----- 当链表为空的时候，直接将新节点设置为头节点
            head = newNode;
        } else {
            // ----- 当链表不为空的时候，将新节点作为尾节点的后继节点；再将尾节点作为新节点的前继节点
            tail.setNext(newNode);
            newNode.setLast(tail);
        }
        // ----- 将新节点设置为尾节点，并且把长度+1，然后返回
        tail = newNode;
        size++;
        return head;
    }

    /**
     * 查找节点
     *
     * @param value 值
     * @return 链表
     */
    public DoubleNode<E> select(E value) {
        if (head == null || value == null) {
            // ----- 当前链表或值为null时直接返回null
            return null;
        }
        DoubleNode<E> node = head;
        while (node != null) {
            if (value.equals(node.getValue())) {
                // ----- 找到链表中第一个节点的值和value相同的节点，返回
                return node;
            }
            node = node.getNext();
        }
        // ----- 没找到
        return null;
    }

    /**
     * 修改链表
     *
     * @param oldVal 旧值
     * @param newVal 新值
     * @return 链表
     */
    public DoubleNode<E> update(E oldVal, E newVal) {
        // ----- 根据旧值找到对应的节点
        DoubleNode<E> node = select(oldVal);
        if (node != null) {
            // ----- 修改
            node.setValue(newVal);
        }
        // ----- 返回当前链表
        return head;
    }

    /**
     * 删除节点
     *
     * @param value 值
     * @return 链表
     */
    public DoubleNode<E> delete(E value) {
        if (head == null || value == null) {
            // ----- 当前链表或值为null时，不做删除，直接返回
            return head;
        }
        // ----- 记录两个位置：node & temp
        DoubleNode<E> node = head, temp = head;
        while (node != null) {
            if (value.equals(node.getValue())) {
                // ----- 找到和值相等的节点
                if (node.equals(head)) {
                    // ----- 如果该节点为头节点：1：将头节点的后继节点的前驱节点设置为null；2：则将头节点设置为其后继节点
                    head.getNext().setLast(null);
                    head = head.getNext();
                } else {
                    // ----- 如果该节点不是头节点，则将该节点记录下来，作为下一个连接点使用
                    temp.setNext(node.getNext());
                    node.getNext().setLast(temp);
                }
                // ----- 删除成功
                size--;
            } else {
                // ----- 值不相等
                temp = node;
            }
            // ----- 到下一个节点
            node = node.getNext();
        }
        return head;
    }

    public int size() {
        return this.size;
    }

    public static void main(String[] args) {
        S03DoubleList<Integer> list = new S03DoubleList<>();
        list.insert(1);
        list.insert(1);
        list.insert(3);
        list.insert(4);
        list.insert(5);

        list.delete(1);

        list.update(4, 9);

        list.select(5);

        log.info("list: [size] {}", list.size());
    }

}
