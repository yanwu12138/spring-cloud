package com.yanwu.spring.cloud.common.data.repository;

import com.yanwu.spring.cloud.common.core.logging.CustomLogger;
import com.yanwu.spring.cloud.common.core.logging.Loggable;
import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class DefaultRepositoryFactoryBean<T extends CustomRepository<S, ID>, S extends BaseObject, ID extends Serializable>
        extends JpaRepositoryFactoryBean<T, S, ID> {

    @Loggable
    protected CustomLogger logger = null;

    /**
     * Returns a {@link RepositoryFactorySupport}.
     *
     * @param entityManager
     * @return
     */
    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new DefaultRepositoryFactory(entityManager, logger);
    }
}