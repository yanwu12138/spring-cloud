package com.yanwu.spring.cloud.common.demo.structure.tree;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/20 10:03.
 * <p>
 * description: 二叉树
 */
public class BitTree<E extends Serializable> {
    private Node<E>[] values;
    private Node<E> root;

    private BitTree() {
        values = new Node[16];
    }

    /*** 插入节点 */
    private void insert(E data) {
        Node<E> newNode = new Node<E>().setItem(data);
        if (root == null) {
            root = newNode;
            return;
        }
        insert(root, newNode);
    }

    private void insert(Node<E> root, Node<E> node) {
        while (root != null) {
            if (root.leftChild == null) {
                root.leftChild = node;
                return;
            } else if (root.rightChild == null) {
                root.rightChild = node;
                return;
            } else {
                insert(root.leftChild, node);
                return;
            }
        }
    }

    /*** 查找结点
     * 查找某个节点，我们必须从根节点开始遍历。
     * ①、查找值比当前节点值大，则搜索右子树；
     * ②、查找值等于当前节点值，停止搜索（终止条件）；
     * ③、查找值小于当前节点值，则搜索左子树；
     **/
    private Node<E> select(E data) {
        if (root == null) {
            return null;
        }
        // ----- 开始递归查找
        return select(root, data);
    }

    private Node<E> select(Node<E> current, E data) {
        if (data.equals(current.item)) {
            System.out.println("====找到了====: " + data);
            return current;
        }
        if (current.leftChild != null) {
            return select(current.leftChild, data);
        }
        if (current.rightChild != null) {
            return select(current.rightChild, data);
        }
        return null;
    }

    /*** 删除节点 */
    private void delete(Node<E> node) {

    }

    public static void main(String[] args) {
        BitTree<String> tree = new BitTree<>();
        tree.insert("A");
        tree.insert("B");
        tree.insert("C");
        tree.insert("D");
        tree.insert("E");
        tree.insert("F");
        tree.insert("G");
        tree.insert("H");
        tree.insert("I");
        tree.insert("J");
        tree.insert("K");
        tree.insert("L");
        tree.insert("M");
        tree.insert("N");
        Node<String> a = tree.select("A");
        System.out.println(a);
    }

    /***
     * 前序遍历
     */
    private static void preorderTraversal() {

    }

    @Data
    @Accessors(chain = true)
    private static final class Node<E extends Serializable> {
        /*** 数据 ***/
        private E item;
        /*** 左子节点 ***/
        private Node<E> leftChild;
        /*** 右子节点 ***/
        private Node<E> rightChild;
    }

}
