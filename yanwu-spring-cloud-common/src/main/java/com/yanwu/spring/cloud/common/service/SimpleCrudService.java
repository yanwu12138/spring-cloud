package com.yanwu.spring.cloud.common.service;



import com.yanwu.spring.cloud.common.data.entity.BaseObject;

import java.io.Serializable;

public interface SimpleCrudService<DO extends BaseObject> extends SimpleRoService<DO> {

	DO create(DO do1) throws Exception;

	DO update(DO do1) throws Exception;

	void delete(Serializable id) throws Exception;

	void delete(Serializable[] ids) throws Exception;

}
