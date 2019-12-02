package com.yanwu.spring.cloud.common.demo;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2019/12/02
 * <p>
 * describe:
 */
public class MyLinkedList<E> implements Iterable {

    private int size;
    private int modCount = 0;
    /*** 开始节点 */
    private Node<E> endMaker;
    /*** 结束节点 */
    private Node<E> beginMaker;

    public MyLinkedList() {
        doClear();
    }

    public void clear() {
        doClear();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void add(E e) {
        add(size(), e);
    }

    private void add(int index, E e) {
        addBefore(getNode(index, 0, size()), e);
    }

    public E get(int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException();
        }
        return getNode(index).data;
    }

    public void set(int index, E e) {
        getNode(index).data = e;
    }

    public void remove(int index) {
        remove(getNode(index));
    }

    private void remove(Node<E> node) {
        node.next.prev = node.prev;
        node.prev.next = node.next;
        size--;
        modCount++;
    }

    private void addBefore(Node<E> node, E e) {
        Node<E> newNode = new Node<>(e, node.prev, node);
        newNode.prev.next = node.prev = newNode;
        size++;
        modCount++;
    }

    private Node<E> getNode(int index) {
        return getNode(index, 0, size() - 1);
    }

    private Node<E> getNode(int index, int lower, int upper) {
        Node<E> node;
        if (index < lower || index > upper) {
            throw new IndexOutOfBoundsException();
        }
        if (index < size() / 2) {
            node = beginMaker.next;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        } else {
            node = endMaker;
            for (int i = size(); i > index; i--) {
                node = node.prev;
            }
        }
        return node;
    }

    private void doClear() {


    }


    @Override
    public Iterator iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator {

        private Node<E> current = beginMaker.next;
        private int expectedModCount = modCount;
        private boolean okToRemove = false;

        @Override
        public boolean hasNext() {
            return current != endMaker;
        }

        @Override
        public E next() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E result = current.data;
            current = current.next;
            okToRemove = true;
            return result;
        }

        @Override
        public void remove() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new IllegalStateException();
            }
            MyLinkedList.this.remove(current.prev);
            expectedModCount++;
            okToRemove = false;
        }
    }

    private static class Node<E> {
        /*** 元素 */
        private E data;
        /*** 前驱 */
        private Node<E> prev;
        /*** 后继 */
        private Node<E> next;

        public Node(E data, Node<E> prev, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }
}
