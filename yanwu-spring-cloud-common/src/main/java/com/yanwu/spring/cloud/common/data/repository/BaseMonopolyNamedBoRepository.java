package com.yanwu.spring.cloud.common.data.repository;

import com.yanwu.spring.cloud.common.data.entity.BaseMonopolyNamedBo;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseMonopolyNamedBoRepository<T extends BaseMonopolyNamedBo> extends BaseDoRepository<T> {

	List<T> findByName(String name);

}
