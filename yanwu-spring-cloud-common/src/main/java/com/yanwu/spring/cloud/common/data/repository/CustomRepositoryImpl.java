package com.yanwu.spring.cloud.common.data.repository;

import com.yanwu.spring.cloud.common.core.logging.CustomLogger;
import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Transactional(readOnly = true, rollbackFor = Exception.class)
public class CustomRepositoryImpl<T extends BaseObject, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements CustomRepository<T, ID> {

    private CustomLogger logger;
    protected EntityManager em;
    private SimpleJpaRepository<T, ID> target;
    private JpaEntityInformation<T, ?> entityInformation;
    private Class<?> springDataRepositoryInterface;

    public Class<?> getSpringDataRepositoryInterface() {
        return springDataRepositoryInterface;
    }

    public void setSpringDataRepositoryInterface(Class<?> springDataRepositoryInterface) {
        this.springDataRepositoryInterface = springDataRepositoryInterface;
    }

    protected EntityManager getEntityManager() {
        return em;
    }

    public CustomRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager,
                                Class<?> springDataRepositoryInterface, CustomLogger logger) {
        this(entityInformation, entityManager);
        this.springDataRepositoryInterface = springDataRepositoryInterface;
        this.logger = logger;
    }

    public CustomRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.em = entityManager;
        target = new SimpleJpaRepository<T, ID>(this.entityInformation, em);
    }

    public CustomRepositoryImpl(Class<T> domainClass, EntityManager em) {
        this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(ID id) {
        if (logger != null) {
            logger.info("delete record id={}", id);
        }
        target.delete(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(T entity) {
        if (logger != null) {
            logger.info("delete single entity");
        }
        target.delete(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Iterable<? extends T> entities) {
        if (logger != null) {
            logger.info("delete multiple entities");
        }
        target.delete(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInBatch(Iterable<T> entities) {
        if (logger != null) {
            logger.info("delete multiple entities in batch");
        }
        target.deleteInBatch(entities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
        if (logger != null) {
            logger.info("delete all");
        }
        for (T element : findAll()) {
            target.delete(element);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllInBatch() {
        if (logger != null) {
            logger.info("delete all in batch");
        }
        target.deleteAllInBatch();
    }

    @Override
    public T findOne(ID id) {
        if (logger != null) {
            logger.debug("find one record id={}", id);
        }
        return target.findOne(id);
    }

    @Override
    public boolean exists(ID id) {
        if (logger != null) {
            logger.debug("exists id={}", id);
        }
        return target.exists(id);
    }

    @Override
    public List<T> findAll() {
        if (logger != null) {
            logger.debug("find all");
        }
        return target.findAll();
    }

    @Override
    public List<T> findAll(Iterable<ID> ids) {
        if (logger != null) {
            logger.debug("find all in ids={}", ids);
        }
        return target.findAll(ids);
    }

    @Override
    public List<T> findAll(Sort sort) {
        if (logger != null) {
            logger.debug("find all in sort");
        }
        return target.findAll(sort);
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        if (logger != null) {
            logger.debug("find all in pageable");
        }
        return target.findAll(pageable);
    }

    @Override
    public T findOne(Specification<T> spec) {
        if (logger != null) {
            logger.debug("find one in specification");
        }
        return target.findOne(spec);
    }

    @Override
    public List<T> findAll(Specification<T> spec) {
        if (logger != null) {
            logger.debug("find all in specification");
        }
        return target.findAll(spec);
    }

    @Override
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        if (logger != null) {
            logger.debug("find all in specification and pageable");
        }
        return target.findAll(spec, pageable);
    }

    @Override
    public List<T> findAll(Specification<T> spec, Sort sort) {
        if (logger != null) {
            logger.debug("find all in specification and sort");
        }
        return target.findAll(spec, sort);
    }

    @Override
    public long count(Specification<T> spec) {
        if (logger != null) {
            logger.debug("count in specification");
        }
        return target.count(spec);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> S save(S entity) {
        if (logger != null) {
            logger.info("save entity");
        }
        return target.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> S saveAndFlush(S entity) {
        if (logger != null) {
            logger.info("save and flush entity");
        }
        return target.saveAndFlush(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <S extends T> List<S> save(Iterable<S> entities) {
        if (logger != null) {
            logger.info("save entities");
        }
        return target.save(entities);
    }

    @Override
    public void flush() {
        if (logger != null) {
            logger.info("flush");
        }
        target.flush();
    }

    @Override
    public long count() {
        if (logger != null) {
            logger.info("count");
        }
        return target.count();
    }

    public Class<?> getEntityClass() {
        return entityInformation.getJavaType();
    }

}