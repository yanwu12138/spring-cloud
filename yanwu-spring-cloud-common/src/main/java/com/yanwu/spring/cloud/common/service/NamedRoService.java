package com.yanwu.spring.cloud.common.service;



import com.yanwu.spring.cloud.common.data.entity.BaseObject;

import java.util.List;

public interface NamedRoService<DO extends BaseObject> extends SimpleRoService<DO> {

	List<DO> findByName(String name) throws Exception;

}