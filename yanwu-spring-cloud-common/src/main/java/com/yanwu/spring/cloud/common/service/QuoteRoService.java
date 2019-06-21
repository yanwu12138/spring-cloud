package com.yanwu.spring.cloud.common.service;

import com.yanwu.spring.cloud.common.data.entity.BaseObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuoteRoService<DO extends BaseObject> extends SimpleRoService<DO> {

	Page<DO> findByOwnerId(Long ownerId, Pageable pageRequest) throws Exception;

}