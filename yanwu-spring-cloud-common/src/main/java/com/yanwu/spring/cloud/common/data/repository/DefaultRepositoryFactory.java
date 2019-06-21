package com.yanwu.spring.cloud.common.data.repository;

import com.yanwu.spring.cloud.common.core.logging.CustomLogger;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;

import static org.springframework.data.querydsl.QueryDslUtils.QUERY_DSL_PRESENT;

/**
 * The purpose of this class is to override the default behaviour of the spring
 * JpaRepositoryFactory class. It will produce a CustomRepositoryImpl object
 * instead of SimpleJpaRepository.
 */
public class DefaultRepositoryFactory extends JpaRepositoryFactory {

    @SuppressWarnings("unused")
    private final EntityManager entityManager;
    private static CustomLogger logger = null;

    public DefaultRepositoryFactory(EntityManager entityManager, CustomLogger logger) {
        super(entityManager);
        Assert.notNull(entityManager, "The entityManager must not be null");
        this.entityManager = entityManager;
        DefaultRepositoryFactory.logger = logger;
    }

    @Override
    protected <T, ID extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(
            RepositoryInformation information, EntityManager entityManager) {
        JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
        Class<?> repositoryInterface = information.getRepositoryInterface();

        if (isQueryDslExecutor(repositoryInterface)) {
            return new QueryDslJpaRepository(entityInformation, entityManager);
        } else {
            return new CustomRepositoryImpl(entityInformation, entityManager, repositoryInterface, logger);
        }
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isQueryDslExecutor(metadata.getRepositoryInterface())) {
            return QueryDslJpaRepository.class;
        } else {
            return CustomRepositoryImpl.class;
        }
    }

    /**
     * Returns whether the given repository interface requires a QueryDsl
     * specific implementation to be chosen.
     *
     * @param repositoryInterface
     * @return
     */
    protected boolean isQueryDslExecutor(Class<?> repositoryInterface) {
        return QUERY_DSL_PRESENT && QueryDslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
    }

}