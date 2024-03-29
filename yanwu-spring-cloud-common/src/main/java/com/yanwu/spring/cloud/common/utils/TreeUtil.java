package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.TreeNodeBO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Baofeng Xu
 * @date 2023-03-14 014 16:23:14.
 * <p>
 * description: 节点树结构相关工具（菜单树等）
 */
@Slf4j
public class TreeUtil {
    private static final Long TOP_NODE_ID = 0L;

    private TreeUtil() {
        throw new UnsupportedOperationException("TreeUtil should never be instantiated");
    }

    public static void main(String[] args) {
        TestNode.test();
    }

    /**
     * list结构转换成树结构
     *
     * @param nodes 节点集合
     * @param clazz ? extends TreeNodeBO
     */
    public static <T extends TreeNodeBO<T>> List<T> listToTree(List<T> nodes, Class<T> clazz) {
        return CollectionUtils.isEmpty(nodes) ? Collections.emptyList() : nodesToChild(nodes, TOP_NODE_ID, clazz);
    }

    /**
     * 树结构转换成list结构
     *
     * @param nodes 节点集合
     */
    public static <T extends TreeNodeBO<T>> List<T> treeToList(List<T> nodes, Class<T> clazz) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }
        List<T> nodeList = new ArrayList<>();
        childToNode(nodes, nodeList, clazz);
        return nodeList;
    }

    /**
     * 递归处理节点数据
     *
     * @param nodes    节点集合
     * @param parentId 父节点ID
     * @param clazz    ? extends TreeNodeBO
     */
    private static <T extends TreeNodeBO<T>> List<T> nodesToChild(List<T> nodes, Long parentId, Class<T> clazz) {
        List<T> child = new ArrayList<>();
        if (CollectionUtils.isEmpty(nodes) || parentId == null) {
            return child;
        }
        nodes.forEach(node -> {
            if (node.getParentId().equals(parentId)) {
                T instance = createNodeInstance(node, clazz);
                instance.setParentId(parentId);
                child.add(instance);
            }
        });
        child.forEach(node -> {
            if (isLeaf(nodes, node.getNodeId())) {
                node.setChild(nodesToChild(nodes, node.getNodeId(), clazz));
            }
        });
        return child;
    }

    /**
     * 递归处理节点数据
     *
     * @param nodes  当前递归的节点集合
     * @param result 总结果响应集
     * @param clazz  ? extends TreeNodeBO
     */
    private static <T extends TreeNodeBO<T>> void childToNode(List<T> nodes, List<T> result, Class<T> clazz) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        nodes.forEach(node -> {
            childToNode(node.getNodeId(), nodes, result, clazz);
            if (CollectionUtils.isNotEmpty(node.getChild())) {
                childToNode(node.getChild(), result, clazz);
            }
        });
    }

    /**
     * 递归处理节点数据
     *
     * @param nodeId 当前递归到的节点ID
     * @param nodes  当前递归的节点集合
     * @param result 总结果响应集
     * @param clazz  ? extends TreeNodeBO
     */
    private static <T extends TreeNodeBO<T>> void childToNode(Long nodeId, List<T> nodes, List<T> result, Class<T> clazz) {
        if (CollectionUtils.isEmpty(nodes) || nodeId == null || nodeId.equals(TOP_NODE_ID)) {
            return;
        }
        if (result.stream().anyMatch(node -> nodeId.equals(node.getNodeId()))) {
            return;
        }
        nodes.forEach(node -> {
            if (nodeId.equals(node.getNodeId())) {
                result.add(createNodeInstance(node, clazz));
                if (isTop(node)) {
                    childToNode(node.getParentId(), nodes, result, clazz);
                }
            }
        });
    }

    /**
     * 检查该节点是否有子节点，如果有则递归该节点，找到该节点的子节点
     *
     * @param nodes  节点集合
     * @param nodeId 节点ID
     * @return [true: 不是子节点，继续递归; false: 是子节点，跳出]
     */
    private static <T extends TreeNodeBO<T>> boolean isLeaf(List<T> nodes, Long nodeId) {
        return nodes.stream().anyMatch(item -> nodeId.equals(item.getParentId()));
    }

    /**
     * 检查该节点是否是顶级节点
     *
     * @param node 节点
     * @return [true: 是顶级节点，跳出; false: 不是顶级节点，继续递归]
     */
    private static <T extends TreeNodeBO<T>> boolean isTop(TreeNodeBO<T> node) {
        return node.getParentId() == null || node.getParentId().equals(TOP_NODE_ID);
    }

    /**
     * 获取节点实例
     *
     * @param node  节点数据
     * @param clazz ? extends TreeNodeBO
     */
    private static <T extends TreeNodeBO<T>> T createNodeInstance(T node, Class<T> clazz) {
        T instance = JsonUtil.toObject(JsonUtil.toString(node), clazz);
        instance.setChild(new ArrayList<>());
        return instance;
    }

    @Data
    @Accessors(chain = true)
    @SuppressWarnings("all")
    @EqualsAndHashCode(callSuper = true)
    private static class TestNode extends TreeNodeBO<TestNode> {
        private static final long serialVersionUID = -4375256989348090867L;
        private String nodeName;
        private String nodeCode;

        private static void test() {
            long begin, done;
            List<TestNode> listNode1 = listNodes();
            System.out.println("============================================================");
            System.out.println("| listToTree >> start: " + (begin = System.currentTimeMillis()));
            List<TestNode> treeNode1 = listToTree(listNode1, TestNode.class);
            System.out.println("| listToTree >> done: " + (done = System.currentTimeMillis()) + ", time: " + (done - begin));
            System.out.println("|-----------------------------------------------------------");
            System.out.println("| treeToList >> start: " + (begin = System.currentTimeMillis()));
            List<TestNode> listNode2 = treeToList(treeNode1, TestNode.class);
            System.out.println("| treeToList >> done: " + (done = System.currentTimeMillis()) + ", time: " + (done - begin));
            System.out.println("|-----------------------------------------------------------");
            System.out.println("| listToTree >> start: " + (begin = System.currentTimeMillis()));
            List<TestNode> treeNode2 = listToTree(listNode2, TestNode.class);
            System.out.println("| listToTree >> done: " + (done = System.currentTimeMillis()) + ", time: " + (done - begin));
            System.out.println("|-----------------------------------------------------------");
            System.out.println("| checkListNodes >> start: " + (begin = System.currentTimeMillis()));
            checkListNodes(listNode1, listNode2);
            System.out.println("| checkListNodes >> done: " + (done = System.currentTimeMillis()) + ", time: " + (done - begin));
            System.out.println("============================================================");
            System.out.println(JsonUtil.toString(treeNode2));
        }

        private static void checkListNodes(List<TestNode> listNode1, List<TestNode> listNode2) {
            if (listNode1.size() != listNode2.size()) {
                System.out.println("------- ERROR -------");
                throw new RuntimeException();
            }
            for (TestNode node : listNode1) {
                List<TestNode> collect = listNode2.stream().filter(item -> item.getNodeCode().equals(node.getNodeCode())).collect(Collectors.toList());
                if (collect.size() != 1) {
                    System.out.println("------- ERROR -------");
                    throw new RuntimeException();
                }
            }
        }

        private static List<TestNode> listNodes() {
            List<TestNode> nodes = new ArrayList<>();
            TestNode topNode = new TestNode();
            topNode.setNodeId(1L).setParentId(TOP_NODE_ID);
            topNode.setNodeCode("1").setNodeName(RandomStringUtils.randomAlphanumeric(10));
            nodes.add(topNode);
            int level = 1;
            long index = 2;
            while (index < 10000) {
                TestNode instance = new TestNode();
                String code;
                Long parentId;
                if (RandomUtils.nextInt(1, 10) <= 2) {
                    // ----- 新增顶级节点
                    parentId = TOP_NODE_ID;
                    code = String.valueOf(index);
                } else {
                    // ----- 新增子节点
                    TestNode parentNode = null;
                    int nextInt = RandomUtils.nextInt(1, level);
                    for (TestNode item : nodes) {
                        int matches = StringUtils.countMatches(item.getNodeCode(), "_");
                        if (matches == nextInt && item.getChild().size() < 5) {
                            parentNode = item;
                        }
                    }
                    if (parentNode != null) {
                        if (level < 10) {
                            level++;
                        }
                    } else {
                        parentNode = nodes.stream().findAny().get();
                    }
                    parentId = parentNode.getNodeId();
                    code = parentNode.getNodeCode() + "_" + index;
                }
                instance.setNodeId(index).setParentId(parentId);
                instance.setNodeCode(code).setNodeName(RandomStringUtils.randomAlphanumeric(10));
                nodes.add(instance);
                index++;
            }
            return nodes;
        }
    }

}
