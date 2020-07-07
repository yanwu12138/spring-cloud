package com.yanwu.spring.cloud.common.demo.d07structure.s01list;

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
public class S02LinkedList<E extends Serializable> implements S00List<Node<Integer>> {
    /*** 链表的元素个数 ***/
    private Integer size;
    /*** 链表的头节点 ***/
    private Node<Integer> head;
    /*** 链表的尾节点 ***/
    private Node<Integer> tail;

    public S02LinkedList() {
        this.size = 0;
    }

    /**
     * 插入节点
     *
     * @param node 值
     * @return 链表
     */
    @Override
    public Node<Integer> insert(Node<Integer> node) {
        if (node == null) {
            // ----- 值为null时不插入，直接返回当前链表
            return head;
        }
        if (head == null) {
            // ----- 当链表为空的时候，直接将新节点设置为头节点
            head = node;
        } else {
            // ----- 当链表不为空的时候，将新节点作为尾节点的后继节点
            tail.setNext(node);
        }
        // ----- 将新节点设置为尾节点，并且把长度+1，然后返回
        tail = node;
        size++;
        return head;
    }

    /**
     * 查找节点
     *
     * @param node 值
     * @return 链表
     */
    @Override
    public Node<Integer> select(Node<Integer> node) {
        if (head == null || node == null) {
            // ----- 当前链表或值为null时直接返回null
            return null;
        }
        Node<Integer> temp = head;
        while (temp != null) {
            if (node.getValue().equals(temp.getValue())) {
                // ----- 找到链表中第一个节点的值和value相同的节点，返回
                return temp;
            }
            temp = temp.getNext();
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
    @Override
    public Node<Integer> update(Node<Integer> oldVal, Node<Integer> newVal) {
        // ----- 根据旧值找到对应的节点
        Node<Integer> node = select(oldVal);
        if (node != null) {
            // ----- 修改
            node.setValue(newVal.getValue());
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
    @Override
    public Node<Integer> delete(Node<Integer> value) {
        if (head == null || value == null) {
            // ----- 当前链表或值为null时，不做删除，直接返回
            return head;
        }
        // ----- 记录两个位置：node & temp
        Node<Integer> node = head, temp = head;
        while (node != null) {
            if (value.getValue().equals(node.getValue())) {
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
        S02LinkedList<Node<Integer>> list = new S02LinkedList<>();
        log.info("list: [insert] {}", list.insert(new Node<>(1)));
        log.info("list: [insert] {}", list.insert(new Node<>(1)));
        log.info("list: [insert] {}", list.insert(new Node<>(1)));
        log.info("list: [insert] {}", list.insert(new Node<>(4)));
        log.info("list: [insert] {}", list.insert(new Node<>(1)));
        log.info("list: [insert] {}", list.insert(new Node<>(5)));

        log.info("list: [delete] {}", list.delete(new Node<>(1)));

        log.info("list: [update] {}", list.update(new Node<>(4), new Node<>(9)));

        log.info("list: [select] {}", list.select(new Node<>(5)));

        log.info("list: [size] {}", list.size());
    }

}