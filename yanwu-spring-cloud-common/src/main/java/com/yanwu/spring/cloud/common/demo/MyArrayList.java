package com.yanwu.spring.cloud.common.demo;

import java.util.Iterator;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2019/12/02
 * <p>
 * describe:
 */
public class MyArrayList<E> implements Iterable {

    private static final int DEFAULT_SIZE = 10;
    private int size;
    private E[] data;

    /**
     * 构造函数
     */
    public MyArrayList() {
        new MyArrayList<E>(DEFAULT_SIZE);
    }

    /**
     * 构造函数
     *
     * @param size 集合长度
     */
    public MyArrayList(int size) {
        doClear(size);
    }

    /**
     * 清空集合
     */
    public void clear() {
        doClear(DEFAULT_SIZE);
    }

    /**
     * 获取集合中元素的个数
     *
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * 判断集合是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }

    public void trimToSize() {
        ensureCapacity(size());
    }

    /**
     * 根据角标获取元素
     *
     * @param index 角标
     * @return
     */
    public E get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return data[index];
    }


    /**
     * 添加元素
     *
     * @param e
     */
    public void add(E e) {
        add(size(), e);
    }

    /**
     * 插入元素
     *
     * @param index
     * @param e
     */
    private void add(int index, E e) {
        if (data.length == size()) {
            ensureCapacity(size() * 2 + 1);
        }
        for (int i = this.size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = e;
        size++;
    }

    /**
     * 删除元素
     *
     * @param index
     * @return
     */
    public E remove(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        E e = data[index];
        for (int i = index; i < size() - 1; i++) {
            data[i] = data[i + 1];
        }
        size--;
        return e;
    }

    /**
     * 修改元素
     *
     * @param index
     * @param e
     */
    public void set(int index, E e) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        data[index] = e;
    }


    /**
     * 初始化数组
     *
     * @param size
     */
    private void doClear(int size) {
        this.size = 0;
        ensureCapacity(size);
    }

    /**
     * 扩容函数
     *
     * @param newSize
     */
    private void ensureCapacity(int newSize) {
        if (newSize < size) {
            return;
        }
        E[] old = data;
        data = (E[]) new Object[newSize];
        for (int i = 0; i < old.length; i++) {
            data[i] = old[i];
        }
    }

    /**
     * 迭代器
     *
     * @return
     */
    @Override
    public Iterator iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator {

        private int current = 0;

        @Override
        public boolean hasNext() {
            return current < size();
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new IndexOutOfBoundsException();
            }
            return data[current++];
        }

        @Override
        public void remove() {
            MyArrayList.this.remove(current--);
        }
    }
}
