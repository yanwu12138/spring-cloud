package com.yanwu.spring.cloud.common.demo.structure.tree;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/5/20 10:03.
 * <p>
 * description: 二叉树
 */
@Slf4j
public class BinaryTree<E extends Serializable> {
    private Node<E> root;

    public BinaryTree() {
    }

    public BinaryTree(Node<E> root) {
        this.root = root;
    }

    /**
     * 二叉树清空
     *
     * @param node 清空树
     */
    public void clear(Node<E> node) {
        if (node != null) {
            clear(node.leftChild);
            clear(node.rightChild);
            node = null;
        }
    }

    /**
     * 二叉树清空
     */
    public void clear() {
        clear(root);
    }

    /**
     * 判断树是否为空
     *
     * @return
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * 获取树的高度
     *
     * @return
     */
    public Integer high() {
        return high(root);
    }

    /**
     * 获取树的高度
     *
     * @param node
     * @return
     */
    public Integer high(Node<E> node) {
        if (node == null) {
            return 0;
        }
        int lHigh = high(node.leftChild);
        int rHigh = high(node.rightChild);
        return Math.max(++lHigh, ++rHigh);
    }

    public Integer size() {
        return size(root);
    }

    public Integer size(Node<E> node) {
        if (node == null) {
            return 0;
        }
        return 1 + size(node.leftChild) + size(node.rightChild);
    }

    public Node<E> parent(Node<E> node) {
        return root == null || node.equals(root) ? null : parent(root, node);
    }

    public Node<E> parent(Node<E> root, Node<E> node) {
        if (root == null || node == null) {
            return null;
        }
        if (node.equals(root.leftChild) || node.equals(root.rightChild)) {
            return root;
        }
        if (root.leftChild != null) {
            return parent(root.leftChild, node);
        } else {
            return parent(root.rightChild, node);
        }
    }

    public Node<E> leftChild(Node<E> node) {
        return node.leftChild;
    }

    public Node<E> rightChild(Node<E> node) {
        return node.rightChild;
    }

    public void insert(E item) {
        Node<E> newNode = new Node<>();
        newNode.setItem(item);
        if (root == null) {
            root = newNode;
        } else {
            if (!root.item.equals(item)) {
                insert(root, newNode);
            }
        }
    }

    public void insert(Node<E> root, Node<E> node) {
        if (root.leftChild == null) {
            root.leftChild = node;
        } else if (root.rightChild == null) {
            root.rightChild = node;
        } else {

        }
    }


    /**
     * 先根遍历
     *
     * @param node
     */
    public void preOrder(Node<E> node) {
        if (node == null) {
            return;
        }
        log.info("node item: {}", node.item);
        preOrder(node.leftChild);
        preOrder(node.rightChild);
    }

    /**
     * 中序遍历
     *
     * @param node
     */
    public void inOrder(Node<E> node) {
        if (node == null) {
            return;
        }
        inOrder(node.leftChild);
        log.info("node item: {}", node.item);
        inOrder(node.rightChild);
    }

    /**
     * 后序遍历
     *
     * @param node
     */
    private void postOrder(Node<E> node) {
        if (node == null) {
            return;
        }
        postOrder(node.leftChild);
        postOrder(node.rightChild);
        log.info("node item: {}", node.item);
    }

    /**
     * 按层遍历
     *
     * @param node
     */
    private void layerOrder(Node<E> node) {
        if (node == null) {
            return;
        }
        LinkedList<Node<E>> linkedList = new LinkedList<>();
        linkedList.offer(node);
        while (!linkedList.isEmpty()) {
            node = linkedList.poll();
            if (node.leftChild != null) {
                linkedList.offer(node.leftChild);
            }
            if (node.rightChild != null) {
                linkedList.offer(node.rightChild);
            }
            log.info("node item: {}", node.item);
        }
    }


    public static void main(String[] args) {
        BinaryTree<String> tree = new BinaryTree<>();
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
        log.info("========== 先序遍历 ==========");
        tree.preOrder(tree.root);
        log.info("========== 中序遍历 ==========");
        tree.inOrder(tree.root);
        log.info("========== 后序遍历 ==========");
        tree.postOrder(tree.root);
        log.info("========== 按层遍历 ==========");
        tree.layerOrder(tree.root);
    }

    /***
     * 前序遍历
     */
    private static void preorderTraversal() {

    }

    @Data
    @Accessors(chain = true)
    private static final class Node<E> {
        /*** 数据 ***/
        private E item;
        /*** 左子节点 ***/
        private Node<E> leftChild;
        /*** 右子节点 ***/
        private Node<E> rightChild;
    }

}
