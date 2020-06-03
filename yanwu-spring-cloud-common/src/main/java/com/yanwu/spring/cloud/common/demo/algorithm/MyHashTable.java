package com.yanwu.spring.cloud.common.demo.algorithm;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/21 13:15.
 * <p>
 * description: 散列表
 */
@Slf4j
public class MyHashTable {
    private final Object[] values = new Object[10];

    private int hash(Integer item) {
        return item - 10;
    }

    public void add(Integer item) {
        if (values[hash(item)] == null) {
            LinkedList<Object> linkedList = new LinkedList<>();
            linkedList.addFirst(item);
            values[hash(item)] = linkedList;
        } else {
            LinkedList<Object> linkedList = (LinkedList) values[hash(item)];
            linkedList.addLast(item);
        }
    }

    public void remove(Integer item) {
        values[hash(item)] = null;
    }

    public boolean contains(Integer item) {
        return values[hash(item)] != null && item == values[hash(item)];
    }

    public static void main(String[] args) {
        MyHashTable hashTable = new MyHashTable();
        hashTable.add(13);
        hashTable.add(17);
        log.info("table contains 13: {}", hashTable.contains(13));
        log.info("table contains 15: {}", hashTable.contains(15));
    }
}
