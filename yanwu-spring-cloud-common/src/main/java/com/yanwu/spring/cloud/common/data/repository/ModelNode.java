package com.yanwu.spring.cloud.common.data.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.TreeSet;

@Getter
@Setter
@RequiredArgsConstructor
final public class ModelNode implements Comparable<ModelNode> {
    final private Class<?> modelClass;
    final private Set<ModelNode> subclasses = new TreeSet<>();
    /**
     * For lack of better names - parent means classes that this class depends
     * on; - children are classes that depend on this class.
     */
    final private Set<ModelNode> parents = new TreeSet<>();
    final private Set<ModelNode> children = new TreeSet<>();

    public ModelNode(ModelNode o) {
        this.modelClass = o.modelClass;
        this.subclasses.addAll(o.subclasses);
        this.parents.addAll(o.parents);
        this.children.addAll(o.children);
    }

    @Override
    public int compareTo(ModelNode o) {
        Assert.notNull(o, "ModelNode cmust not be null");
        return modelClass.getName().compareTo(o.modelClass.getName());
    }

    @Override
    public String toString() {
        return String.format("%s [%d, %d, %d]", modelClass.getSimpleName(), subclasses.size(), parents.size(),
                children.size());
    }
}
