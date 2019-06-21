package com.yanwu.spring.cloud.common.service;

import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Collection;

public interface SimpleRoService<DO extends BaseObject> {

    long count() throws Exception;

    DO findOne(Serializable id) throws Exception;

    Page<DO> findByIds(Collection<Long> ids, Pageable pageRequest) throws Exception;

    Page<DO> findAll(Pageable pageRequest) throws Exception;
}
