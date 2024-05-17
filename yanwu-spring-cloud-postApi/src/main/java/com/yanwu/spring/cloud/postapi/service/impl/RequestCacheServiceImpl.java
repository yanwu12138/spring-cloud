package com.yanwu.spring.cloud.postapi.service.impl;


import com.yanwu.spring.cloud.postapi.bo.RequestCacheBO;
import com.yanwu.spring.cloud.postapi.bo.RequestInfo;
import com.yanwu.spring.cloud.postapi.cache.RequestCache;
import com.yanwu.spring.cloud.postapi.service.RibbonService;
import com.yanwu.spring.cloud.postapi.service.RootSwing;
import com.yanwu.spring.cloud.postapi.utils.FileUtil;
import com.yanwu.spring.cloud.postapi.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/5/17 14:45.
 * <p>
 * description:
 */
@Slf4j
@Service("requestCacheService")
public class RequestCacheServiceImpl implements RibbonService {
    private static final String CACHE_FILE_PATH = "/Users/xubaofeng/devTool/postApi/requestCache.json";
    private static final String TREE_ROOT_NAME = "所有请求";

    @Override
    public void createRibbon(RootSwing window) {
        try {
            JPanel requestPanel = new JPanel();
            List<RequestCacheBO> requests = JsonUtil.toObjectList(new String(FileUtil.read(CACHE_FILE_PATH)), RequestCacheBO.class);
            requestPanel.setLayout(new BorderLayout());
            requestPanel.setPreferredSize(new Dimension(300, 0));
            requestPanel.add(createRequestTree(requests));
            window.add(requestPanel, BorderLayout.WEST);
        } catch (Exception e) {
            log.error("init requestCacheService failed.", e);
            System.exit(-1);
        }
    }

    /**
     * 给请求树绑定点击事件
     */
    private JTree createRequestTree(List<RequestCacheBO> requests) {
        JTree requestTree = new JTree(createTreeRoot(requests));
        requestTree.addTreeSelectionListener(node -> {
            String path = buildRequestPath(node.getPath());
            if (StringUtils.isBlank(path)) {
                return;
            }
            System.out.println(path);
            RequestInfo<?> requestInfo = RequestCache.findRequestInfo(path);
            System.out.println(JsonUtil.toString(requestInfo));
            // TODO 根据requestInfo，将结果填充到发送请求区
        });
        return requestTree;
    }

    /**
     * 获取被选中的节点在树中的全路径
     */
    private String buildRequestPath(TreePath paths) {
        TreePath parentPath = paths.getParentPath();
        if (parentPath == null) {
            return null;
        }
        String[] nodes = new String[paths.getParentPath().getPath().length];
        int index = 0;
        for (int i = 0; i < paths.getParentPath().getPath().length; i++) {
            String nodeName = paths.getParentPath().getPath()[i].toString();
            if (nodeName.equals(TREE_ROOT_NAME)) {
                continue;
            }
            nodes[index++] = nodeName;
        }
        nodes[index] = paths.getLastPathComponent().toString();
        return String.join(">", nodes);
    }

    /**
     * 构建请求树
     */
    private DefaultTreeModel createTreeRoot(List<RequestCacheBO> requests) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(TREE_ROOT_NAME);
        if (CollectionUtils.isEmpty(requests)) {
            return new DefaultTreeModel(root);
        }
        for (RequestCacheBO request : requests) {
            createTreeNode(request, root);
        }
        return new DefaultTreeModel(root);
    }

    /**
     * 构建请求树节点
     */
    private void createTreeNode(RequestCacheBO request, DefaultMutableTreeNode root) {
        if (request == null || StringUtils.isBlank(request.getName())) {
            return;
        }
        if (request.isType()) {
            // ----- 文件夹
            DefaultMutableTreeNode children = new DefaultMutableTreeNode(request.getName());
            if (CollectionUtils.isNotEmpty(request.getChildren())) {
                for (RequestCacheBO childRequest : request.getChildren()) {
                    createTreeNode(childRequest, children);
                }
            }
            root.add(children);
        } else {
            // ----- 请求
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(request.getName());
            root.add(node);
            RequestCache.createCache(StringUtils.join(node.getUserObjectPath(), ">"), request);
        }
    }

}
