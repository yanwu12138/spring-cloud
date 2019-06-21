package com.yanwu.spring.cloud.common.data.repository;

import com.yanwu.spring.cloud.common.data.entity.BaseQuoteBo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@NoRepositoryBean
public interface BaseQuoteBoRepository<T extends BaseQuoteBo> extends BaseDoRepository<T>, ShardedResourceRepository {

	@Query("SELECT e FROM #{#entityName} e WHERE (e.ownerId = ?1 OR e.ownerId = -1)")
    Page<T> findByOwnerId(final Long ownerId, final Pageable pageRequest);

	List<T> findByOwnerId(final Long ownerId);

	List<T> findByOwnerIdAndIdNotIn(final Long ownerId, final Set<Long> ids);

	long countByOwnerId(final Long ownerId);

	T findOneByOwnerId(final Long ownerId);

	@Transactional
	int deleteByOwnerId(Long ownerId);

	@Query("SELECT e.id FROM #{#entityName} e WHERE e.ownerId = ?1")
	List<Long> findIdByOwnerId(final Long ownerId);

}