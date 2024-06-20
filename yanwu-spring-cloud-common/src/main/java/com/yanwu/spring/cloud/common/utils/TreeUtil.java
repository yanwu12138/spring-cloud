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

import java.nio.charset.StandardCharsets;
import java.util.*;
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

    public static void main(String[] args) throws Exception {
        TestNode.test();
    }

    /**
     * list结构转换成树结构
     *
     * @param nodes 节点集合
     */
    public static <T extends TreeNodeBO<T>> List<T> listToTree(List<T> nodes) {
        List<T> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(nodes)) {
            return result;
        }
        Map<Long, T> nodeMap = nodes.stream().collect(Collectors.toMap(T::getNodeId, item -> item, (x, y) -> x, LinkedHashMap::new));
        nodes.clear();
        nodeMap.forEach((id, node) -> {
            T parentNode = nodeMap.get(node.getParentId());
            if (parentNode != null) {
                parentNode.getChild().add(node);
            } else {
                result.add(node);
            }
        });
        nodeMap.clear();
        result.removeIf(item -> !item.getParentId().equals(TOP_NODE_ID));
        return result;
    }

    /**
     * 树结构转换成list结构
     *
     * @param nodes 节点集合
     */
    public static <T extends TreeNodeBO<T>> List<T> treeToList(List<T> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }
        List<T> result = childToNode(nodes);
        result.forEach(item -> item.getChild().clear());
        return result.stream().sorted(Comparator.comparing(T::getNodeId)).collect(Collectors.toList());
    }

    /**
     * 递归处理节点数据
     *
     * @param nodes 当前递归的节点集合
     */
    private static <T extends TreeNodeBO<T>> List<T> childToNode(List<T> nodes) {
        List<T> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(nodes)) {
            return result;
        }
        nodes.forEach(node -> {
            result.add(node);
            result.addAll(childToNode(node.getChild()));
        });
        return result;
    }

    @Data
    @Accessors(chain = true)
    @SuppressWarnings("all")
    @EqualsAndHashCode(callSuper = true)
    private static class TestNode extends TreeNodeBO<TestNode> {
        private static final long serialVersionUID = -4375256989348090867L;
        private String nodeName;
        private String nodeCode;

        private static void test() throws Exception {
            String filepath = "/Users/xubaofeng/yanwu/file/testTree.json";

            // ----- 创建测试数据
//            FileUtil.deleteFile(filepath);
//            FileUtil.appendWrite(filepath, JsonUtil.toString(listNodes()).getBytes(StandardCharsets.UTF_8));

            // ----- 读取测试数据
            long begin, done;
            String json = new String(FileUtil.read(filepath), StandardCharsets.UTF_8);
            List<TestNode> listNode1 = JsonUtil.toObjectList(json, TestNode.class);
            listNode1 = listNode1.stream().sorted(Comparator.comparing(TestNode::getNodeId)).collect(Collectors.toList());
            System.out.println("============================================================");
            System.out.println("| listToTree >> start: " + (begin = System.currentTimeMillis()));
            List<TestNode> treeNode1 = listToTree(listNode1);
            System.out.println("| listToTree >> done: " + (done = System.currentTimeMillis()) + ", time: " + (done - begin));
            System.out.println("|-----------------------------------------------------------");
            System.out.println("| treeToList >> start: " + (begin = System.currentTimeMillis()));
            List<TestNode> listNode2 = treeToList(treeNode1);
            System.out.println("| treeToList >> done: " + (done = System.currentTimeMillis()) + ", time: " + (done - begin));
            System.out.println("|-----------------------------------------------------------");
            System.out.println("| listToTree >> start: " + (begin = System.currentTimeMillis()));
            List<TestNode> treeNode2 = listToTree(listNode2);
            System.out.println("| listToTree >> done: " + (done = System.currentTimeMillis()) + ", time: " + (done - begin));
            System.out.println("|-----------------------------------------------------------");
            System.out.println("| check >> start: " + (begin = System.currentTimeMillis()));
            System.out.println("| check list: " + listNode1.equals(listNode2));
            System.out.println("| check tree: " + treeNode1.equals(treeNode2));
            System.out.println("| check >> done: " + (done = System.currentTimeMillis()) + ", time: " + (done - begin));
            System.out.println("============================================================");
        }

        private static List<TestNode> listNodes() {
            List<TestNode> nodes = new ArrayList<>();
            TestNode topNode = new TestNode();
            topNode.setNodeId(1L).setParentId(TOP_NODE_ID);
            topNode.setNodeCode("1").setNodeName(RandomStringUtils.randomAlphanumeric(10));
            nodes.add(topNode);
            int level = 1;
            long index = 2;
            while (index < 100_000) {
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
