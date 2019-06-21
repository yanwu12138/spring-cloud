package com.yanwu.spring.cloud.common.data.repository;

import com.yanwu.spring.cloud.common.data.entity.BaseDo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@NoRepositoryBean
public interface BaseDoRepository<T extends BaseDo<Long>> extends CustomRepository<T, Long> {

	List<T> findByIdIn(final Collection<Long> ids);

	Page<T> findByIdIn(final Collection<Long> ids, final Pageable pageable);

	@Modifying
	@Transactional
	@Query("DELETE FROM #{#entityName} e WHERE e.createdAt < ?1")
	int deleteByCreatedAtBefore(final Date createdAt);

}