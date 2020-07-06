package com.yanwu.spring.cloud.common.demo.d07structure;

import com.yanwu.spring.cloud.common.demo.d07structure.s00model.Node;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-07-05 22:30:08.
 * <p>
 * describe: 单链表
 */
@Slf4j
public class S01LinkedList<E extends Serializable> {
    /*** 链表的元素个数 ***/
    private Integer size;
    /*** 链表的头节点 ***/
    private Node<E> head;
    /*** 链表的尾节点 ***/
    private Node<E> tail;

    public S01LinkedList() {
        this.size = 0;
    }

    /**
     * 插入节点
     *
     * @param value 值
     * @return 链表
     */
    public Node<E> insert(E value) {
        if (value == null) {
            // ----- 值为null时不插入，直接返回当前链表
            return head;
        }
        // ----- 创建一个新节点
        Node<E> newNode = new Node<>(value);
        if (head == null) {
            // ----- 当链表为空的时候，直接将新节点设置为头节点
            head = newNode;
        } else {
            // ----- 当链表不为空的时候，将新节点作为尾节点的后继节点
            tail.setNext(newNode);
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
    public Node<E> select(E value) {
        if (head == null || value == null) {
            // ----- 当前链表或值为null时直接返回null
            return null;
        }
        Node<E> node = head;
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
    public Node<E> update(E oldVal, E newVal) {
        // ----- 根据旧值找到对应的节点
        Node<E> node = select(oldVal);
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
    public Node<E> delete(E value) {
        if (head == null || value == null) {
            // ----- 当前链表或值为null时，不做删除，直接返回
            return head;
        }
        // ----- 记录两个位置：node & temp
        Node<E> node = head, temp = head;
        while (node != null) {
            if (value.equals(node.getValue())) {
                // ----- 找到和值相等的节点
                if (node.equals(head)) {
                    // ----- 如果该节点为头节点，则将头节点设置为其后继节点
                    head = head.getNext();
                } else {
                    // ----- 如果该节点不是头节点，则将该节点记录下来，作为下一个连接点使用
                    temp.setNext(node.getNext());
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

    private int size() {
        return this.size;
    }

    public static void main(String[] args) {
        S01LinkedList<Integer> list = new S01LinkedList<>();
        log.info("list: [insert] {}", list.insert(1));
        log.info("list: [insert] {}", list.insert(1));
        log.info("list: [insert] {}", list.insert(1));
        log.info("list: [insert] {}", list.insert(4));
        log.info("list: [insert] {}", list.insert(1));
        log.info("list: [insert] {}", list.insert(5));

        log.info("list: [delete] {}", list.delete(1));

        log.info("list: [update] {}", list.update(4, 9));

        log.info("list: [select] {}", list.select(5));

        log.info("list: [size] {}", list.size());
    }

}