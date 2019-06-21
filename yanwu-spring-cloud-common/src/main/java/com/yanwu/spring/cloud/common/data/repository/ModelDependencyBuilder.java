package com.yanwu.spring.cloud.common.data.repository;

import com.yanwu.spring.cloud.common.core.logging.LoggerFactory;
import com.yanwu.spring.cloud.common.core.utils.ReflectionUtil;
import com.yanwu.spring.cloud.common.data.entity.BaseDo;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.*;

final public class ModelDependencyBuilder {

    final static private Logger logger = LoggerFactory.getLogger(ModelDependencyBuilder.class);

    final private Map<Class<?>, ModelNode> nodeMap = new HashMap<>(200);

    public ModelDependencyBuilder(Collection<Class<?>> modelClasses) {
        populateModelClasses(modelClasses);
        populateSubclasses();
        populateDependencies();
    }

    public List<Class<?>> getLinearDependencyClasses() {
        List<ModelNode> list = getLinearDependencies();
        List<Class<?>> classList = new ArrayList<>(100);
        for (ModelNode n : list) {
            classList.add(n.getModelClass());
        }
        return classList;
    }

    public Map<Class<?>, ModelNode> getModel() {
        return nodeMap;
    }

    private void populateModelClasses(Collection<Class<?>> seedModelClasses) {
        for (Class<?> c : seedModelClasses) {
            if (nodeMap.containsKey(c)) {
                continue;
            }
            nodeMap.put(c, new ModelNode(c));
            Class<?> superClass = c;
            while (true) {
                superClass = superClass.getSuperclass();
                if (ReflectionUtil.isDoClass(superClass) == false
                        || superClass.getName().contains(BaseDo.class.getSimpleName())) {
                    break;
                }
                if (nodeMap.containsKey(superClass) == false) {
                    nodeMap.put(superClass, new ModelNode(superClass));
                }
            }
            for (Field f : ReflectionUtil.getAllFieldsAsMap(c).values()) {
                if (Collection.class.isAssignableFrom(f.getType()) == false) {
                    continue;
                }
                Class<?> type = ReflectionUtil.getFieldGenericType(f);
                if (ReflectionUtil.isDoClass(type) && nodeMap.containsKey(type) == false) {
                    nodeMap.put(type, new ModelNode(type));
                }
            }
        }
    }

    private void populateSubclasses() {
        for (ModelNode n : nodeMap.values()) {
            ModelNode sn = getSuperClassNode(n);
            while (true) {
                if (sn == null) {
                    break;
                }
                addSubClass(sn, n);
                sn = getSuperClassNode(sn);
            }
        }
    }

    private void addSubClass(ModelNode node, ModelNode subclassNode) {
        node.getSubclasses().add(subclassNode);
        while ((node = getSuperClassNode(node)) != null) {
            node.getSubclasses().add(subclassNode);
        }
    }

    private ModelNode getSuperClassNode(ModelNode node) {
        Class<?> superClass = node.getModelClass().getSuperclass();
        return nodeMap.get(superClass);
    }

    private void populateDependencies() {
        List<ModelNode> nodesInCircularDependencies = new ArrayList<ModelNode>(5);
        for (ModelNode n : nodeMap.values()) {
            List<ModelNode> dependencies = getDependencies(n);
            for (ModelNode parent : dependencies) {
                n.getParents().add(parent);
                parent.getChildren().add(n);
                if (dependencies.contains(n) || parent.getParents().contains(n)) {
                    nodesInCircularDependencies.add(n);
                }
            }
        }
        for (ModelNode n : nodesInCircularDependencies) {
            removeNode(nodeMap, n);
            logger.warn(String.format("Removed class %s from model due to circular dependencies.",
                    n.getModelClass().getSimpleName()));
        }
    }

    private List<ModelNode> getDependencies(ModelNode node) {
        List<ModelNode> dependencies = new ArrayList<>(5);
        Assert.notNull(node, "ModelNode must not be null");
        for (Field f : ReflectionUtil.getAllFieldsAsMap(node.getModelClass()).values()) {
            Class<?> type = null;
            if (Collection.class.isAssignableFrom(f.getType())) {
                type = ReflectionUtil.getFieldGenericType(f);
            } else {
                type = f.getType();
            }

            // If it's a DO
            if (ReflectionUtil.isDoClass(type)) {
                boolean isDependency = false;
                isDependency |= f.getAnnotation(OneToOne.class) != null;
                isDependency |= f.getAnnotation(OneToMany.class) != null;
                isDependency |= f.getAnnotation(ManyToOne.class) != null;
                isDependency |= f.getAnnotation(ManyToMany.class) != null;
                isDependency |= f.getAnnotation(ElementCollection.class) != null;
                if (isDependency) {
                    ModelNode dependencyNode = nodeMap.get(type);
                    Assert.notNull(dependencyNode, "Model node not found: " + type.getSimpleName());
                    dependencies.add(dependencyNode);
                    dependencies.addAll(dependencyNode.getSubclasses());
                }
            }
        }
        return dependencies;
    }

    private List<ModelNode> getLinearDependencies() {
        // Duplicate current model
        Map<Class<?>, ModelNode> anotherNodeMap = new HashMap<>(100);
        for (Class<?> c : nodeMap.keySet()) {
            anotherNodeMap.put(c, new ModelNode(nodeMap.get(c)));
        }
        // Produce linear dependency list by reducing leaf nodes
        List<ModelNode> processed = new ArrayList<ModelNode>();
        while (anotherNodeMap.size() > 0) {
            List<ModelNode> leafNodes = removeRootNodes(anotherNodeMap);
            Assert.isTrue(leafNodes.size() > 0, "leafNodes size must larger than 0");
            processed.addAll(leafNodes);
        }
        return processed;
    }

    private List<ModelNode> removeRootNodes(Map<Class<?>, ModelNode> map) {
        List<ModelNode> list = new ArrayList<ModelNode>(10);
        for (ModelNode n : map.values()) {
            if (n.getParents().isEmpty()) {
                list.add(n);
            }
        }
        for (ModelNode n : list) {
            removeNode(map, n);
        }
        return list;
    }

    private void removeNode(Map<Class<?>, ModelNode> map, ModelNode node) {
        map.remove(node.getModelClass());
        for (ModelNode n : map.values()) {
            n.getParents().remove(node);
            n.getChildren().remove(node);
        }
    }

    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        return toString(indent, nodeMap);
    }

    private String toString(String indent, Map<Class<?>, ModelNode> map) {
        StringBuilder sb = new StringBuilder("{\n");
        for (ModelNode node : map.values()) {
            sb.append(indent + "  ").append(node.getModelClass().getSimpleName()).append(" = {\n");
            if (node.getSubclasses().size() > 0) {
                sb.append(indent + "    ").append("subclasses = {\n");
                for (ModelNode sn : node.getSubclasses()) {
                    sb.append(indent + "      " + sn.getModelClass().getSimpleName()).append("\n");
                }
                sb.append(indent + "    }\n");
            }
            if (node.getParents().size() > 0) {
                sb.append(indent + "    ").append("parents = {\n");
                for (ModelNode pn : node.getParents()) {
                    sb.append(indent + "      " + pn.getModelClass().getSimpleName()).append("\n");
                }
                sb.append(indent + "    }\n");
            }
            if (node.getChildren().size() > 0) {
                sb.append(indent + "    ").append("children = {\n");
                for (ModelNode cn : node.getChildren()) {
                    sb.append(indent + "      " + cn.getModelClass().getSimpleName()).append("\n");
                }
                sb.append(indent + "    }\n");
            }
            sb.append(indent + "  }\n");
        }
        sb.append(indent + "}\n");
        return sb.toString();
    }

}
