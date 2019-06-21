package com.yanwu.spring.cloud.common.data.repository;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

@Slf4j
@Component
@SuppressWarnings("rawtypes")
final public class ModelMetadata implements ApplicationContextAware, InitializingBean {

    @RequiredArgsConstructor
    final private class RepoHolder {
        final private Class<?> repoClass;
        private JpaRepository repo;

        private JpaRepository get() {
            if (repo == null) {
                synchronized (this) {
                    if (repo == null) {
                        repo = (JpaRepository) ctx.getBean(repoClass);
                    }
                }
            }
            return repo;
        }
    }

    @RequiredArgsConstructor
    @ToString
    final private class ModelClassRank implements Comparable<ModelClassRank> {
        final private Integer rank;
        final Class<?> modelClass;

        @Override
        public int compareTo(ModelClassRank o) {
            Assert.notNull(o, "ModelClassRank must not be null");
            return rank.compareTo(o.rank);
        }
    }

    private ApplicationContext ctx;

    @Autowired(required = false)
    private ModelMetadataSuppliment suppliment;

    /**
     * Mapping between model classes and their repository
     */
    private Map<Class<?>, RepoHolder> repoMap = new HashMap<Class<?>, RepoHolder>(200);

    /**
     * Each node contains the following type of information
     * <p>
     * - All subclasses (DO class only) - All model classes that depend on it
     * (children) - All model classes that it depends on (parents)
     * <p>
     * The current implementation doesn't yet handle bi-directional
     * dependencies, its essentially a tree that can be traversed.
     */
    private Map<Class<?>, ModelNode> modelMap;

    /**
     * Mapping from simple class name to model node
     */
    private Map<String, ModelNode> simpleClassNameMap = new HashMap<>(200);

    /**
     * Linear model dependency list ordered by ranking. Classes with lower
     * ranking are guaranteed not dependent on classes with higher ranking,
     * however vice versa is not necessary true.
     */
    private Map<Class<?>, ModelClassRank> rankMap = new HashMap<>(200);

    /**
     * Get JPA repository for the given model class
     */
    public JpaRepository getRepository(Class<?> modelClass) {
        RepoHolder repoHolder = repoMap.get(modelClass);
        if (repoHolder != null) {
            JpaRepository repo = repoHolder.get();
            Assert.notNull(repo, "Repository not found for " + modelClass.getSimpleName());
            return repo;
        }
        log.warn("Model {} has no Repository.", modelClass.getSimpleName());
        return null;
    }

    /**
     * Get JPA repository for the given model object
     */
    public JpaRepository getRepository(Object modelObject) {
        return getRepository(modelObject.getClass());
    }

    /**
     * Get classes that the given model class depends on
     */
    public Class<?>[] getDependingOnClasses(Class<?> modelClass) {
        ModelNode node = modelMap.get(modelClass);
        Assert.notNull(node, "ModelNode must not be null");
        return toClassArray(node.getParents());
    }

    /**
     * Get classes that depend on the given model class
     */
    public Class<?>[] getDependentClasses(Class<?> modelClass) {
        ModelNode node = modelMap.get(modelClass);
        Assert.notNull(node, "ModelNode must not be null");
        return toClassArray(node.getChildren());
    }

    /**
     * Reorder all model classes, so that all classes in front of a particular
     * class do not depend on it. However, vice versa is not necessarily true.
     */
    public Class<?>[] getOrderedModelClasses() {
        Class<?>[] classes = modelMap.keySet().toArray(new Class<?>[modelMap.size()]);
        return reorderModelClasses(classes);
    }

    /**
     * Reorder the given class array, so that all classes in front of a
     * particular class do not depend on it. However, vice versa is not
     * necessarily true.
     */
    public Class<?>[] reorderModelClasses(Class<?>[] classes) {
        Set<ModelClassRank> set = new TreeSet<>();
        for (Class<?> c : classes) {
            ModelClassRank r = rankMap.get(c);
            Assert.notNull(r, String.format("Model node for %s not found", c.getSimpleName()));
            set.add(r);
        }
        int i = 0;
        for (ModelClassRank r : set) {
            classes[i++] = r.modelClass;
        }
        return classes;
    }

    /**
     * Get the model class node for a model class
     */
    public ModelNode getModelNode(Class<?> clazz) {
        return modelMap.get(clazz);
    }

    /**
     * Get the class object based on simple model class name
     */
    public Class<?> getModelClass(String simpleClassName) {
        ModelNode node = simpleClassNameMap.get(simpleClassName);
        return node == null ? null : node.getModelClass();
    }

    /**
     * Get all model classes
     */
    public Set<Class<?>> getAllModelClasses() {
        return Collections.unmodifiableSet(modelMap.keySet());
    }

    private Class<?>[] toClassArray(Collection<ModelNode> nodes) {
        List<Class<?>> result = new ArrayList<Class<?>>(nodes.size());
        for (ModelNode n : nodes) {
            result.add(n.getModelClass());
        }
        return result.toArray(new Class<?>[nodes.size()]);
    }

    @SuppressWarnings("unused")
    private void verifyRepoMap() {
        // Force to load all repositories. Spring internally loads beans
        // dynamically,
        // in order to produce metadata, we will need the complete list of
        // repositories.
        // The load time is in the order of seconds.
        ctx.getBeansOfType(JpaRepository.class);
        // Get Spring internal metadata
        Map<String, RepositoryFactoryBeanSupport> map = ctx.getBeansOfType(RepositoryFactoryBeanSupport.class);
        // Verify metadata
        Set<Class<?>> modelClassesFromAppCtx = new HashSet<>(100);
        for (RepositoryFactoryBeanSupport s : map.values()) {
            modelClassesFromAppCtx.add(s.getEntityInformation().getJavaType());
        }
        for (Class<?> modelClass : modelClassesFromAppCtx) {
            Assert.isTrue(repoMap.containsKey(modelClass),
                    String.format("Model class %s not detected, but it should be", modelClass.getSimpleName()));
        }
        for (Class<?> modelClass : repoMap.keySet()) {
            if (modelClassesFromAppCtx.contains(modelClass) == false) {
                log.warn(String.format("Repository for model class %s not found in application context",
                        modelClass.getSimpleName()));
            }
        }
    }

    public void setPackagesToScan(String... packages) {
        packageToScan = new String[defaultPackages.length + packages.length];
        System.arraycopy(defaultPackages, 0, packageToScan, defaultPackages.length - 1, defaultPackages.length);
        System.arraycopy(packages, 0, packageToScan, defaultPackages.length, packages.length);

    }

    private final static String[] defaultPackages = new String[]{"com.yanwu"};

    protected String[] packageToScan = defaultPackages;

    private ModelDependencyBuilder createModelDependencyBuilder() {
        List<Class<?>> modelClasses = new LinkedList<>();
        modelClasses.addAll(repoMap.keySet());
        if (suppliment != null) {
            for (Class<?> c : suppliment.getModelClasses()) {
                modelClasses.add(c);
            }
        }
        ModelDependencyBuilder builder = new ModelDependencyBuilder(modelClasses);
        return builder;
    }

    private void buildRankMap(ModelDependencyBuilder builder) {
        List<Class<?>> list = builder.getLinearDependencyClasses();
        int rank = 1;
        for (Class<?> c : list) {
            ++rank;
            rankMap.put(c, new ModelClassRank(rank, c));
        }
    }

    private void buildModelMap(ModelDependencyBuilder builder) {
        modelMap = builder.getModel();
    }

    private void buildSimpleClassNameMap(ModelDependencyBuilder builder) {
        for (ModelNode node : modelMap.values()) {
            String simpleClassName = node.getModelClass().getSimpleName();
            ModelNode existingNode = simpleClassNameMap.get(simpleClassName);
            if (existingNode != null) {
                throw new RuntimeException(String.format("Identical simple class name not allowed: %s, %s",
                        node.getModelClass().getName(), existingNode.getModelClass().getName()));
            }
            simpleClassNameMap.put(simpleClassName, node);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        ModelDependencyBuilder builder = createModelDependencyBuilder();
        buildRankMap(builder);
        buildModelMap(builder);
        buildSimpleClassNameMap(builder);
        log.info("{} initialized. ({})", getClass().getSimpleName(), sw);

    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        log.info("setApplicationContext ={}", ctx);
        this.ctx = ctx;
    }

}